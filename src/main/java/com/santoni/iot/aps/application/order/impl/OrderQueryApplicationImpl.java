package com.santoni.iot.aps.application.order.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.order.OrderQueryApplication;
import com.santoni.iot.aps.application.order.assembler.OrderAssembler;
import com.santoni.iot.aps.application.order.assembler.ProduceOrderDTOAssembler;
import com.santoni.iot.aps.application.order.assembler.WeavingOrderDTOAssembler;
import com.santoni.iot.aps.application.order.context.BuildProduceDemandContext;
import com.santoni.iot.aps.application.order.context.BuildProduceOrderContext;
import com.santoni.iot.aps.application.order.dto.OverviewOrderDTO;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.order.dto.WeavingOrderDTO;
import com.santoni.iot.aps.application.order.query.*;
import com.santoni.iot.aps.application.plan.dto.factory.ProduceOrderDemandDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.service.BomDomainService;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.repository.FactoryPlanRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderQueryApplicationImpl implements OrderQueryApplication {

    @Autowired
    private BomDomainService bomDomainService;

    @Autowired
    private ResourceDomainService resourceDomainService;

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private FactoryPlanRepository factoryPlanRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private OrderAssembler orderAssembler;

    @Autowired
    private WeavingOrderDTOAssembler weavingOrderDTOAssembler;

    @Autowired
    private ProduceOrderDTOAssembler produceOrderDTOAssembler;

    @Override
    public PageResult<WeavingOrderDTO> pageQueryWeavingOrder(PageWeavingOrderQuery query) {
        var search = orderAssembler.composeSearchWeavingOrder(query);
        var pageRes = weavingOrderRepository.pageQueryWeavingOrder(search);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        return PageResult.fromPageData(assembleWeavingOrderDTO(pageRes.getData()), pageRes);
    }

    @Override
    public PageResult<ProduceOrderDTO> pageQueryProduceOrder(ProduceOrderQuery query) {
        var search = orderAssembler.composeSearchProduceOrder(query);
        var pageRes = produceOrderRepository.pageQueryProduceOrder(search);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        var context = prepareComposeProduceOrderContext(pageRes.getData());
        var produceOrderList = pageRes.getData().stream()
                .map(it -> produceOrderDTOAssembler.convertToProduceOrderDTO(it, context)).toList();
        return PageResult.fromPageData(produceOrderList, pageRes);
    }

    @Override
    public List<WeavingOrderDTO> listWeavingOrderById(ListWeavingOrderByIdQuery query) {
        if (CollectionUtils.isEmpty(query.orderIds())) {
            return List.of();
        }
        var orderList = weavingOrderRepository.listWeavingOrderById(query.orderIds().stream().map(WeavingOrderId::new).toList());
        if (CollectionUtils.isEmpty(orderList)) {
            return List.of();
        }
        return assembleWeavingOrderDTO(orderList);
    }

    @Override
    public ProduceOrderDTO queryProduceOrderByCode(ProduceOrderDetailByCodeQuery query) {
        var order = produceOrderRepository.produceOrderDetailByCode(new ProduceOrderCode(query.orderCode()));
        if (null == order) {
            throw new IllegalArgumentException("生产单不存在,code:" + query.orderCode());
        }
        var context = prepareComposeProduceOrderContext(order);
        return produceOrderDTOAssembler.convertToProduceOrderDTO(order, context);
    }

    @Override
    public OverviewOrderDTO overviewOrder() {
        var produceOrderList = produceOrderRepository.unfinishedProduceOrder();
        if (CollectionUtils.isEmpty(produceOrderList)) {
            return new OverviewOrderDTO();
        }
        var weavingOrderList = weavingOrderRepository.listWeavingOrderByProduceOrderIds(produceOrderList.stream().map(ProduceOrder::getId).toList());
        return countOrder(produceOrderList, weavingOrderList);
    }

    @Override
    public ProduceOrderDemandDTO queryProduceOrderDemand(ProduceOrderDetailQuery query) {
        var produceOrder = produceOrderRepository.produceOrderDetailById(new ProduceOrderId(query.orderId()));
        if (null == produceOrder) {
            throw new IllegalArgumentException("生产订单不存在:" + query.orderId());
        }
        var context = prepareProduceDemandContext(produceOrder);

        return produceOrderDTOAssembler.assembleOrderDemandDTO(produceOrder.getCode(), context);
    }

    @Override
    public List<Long> canWeaveMachine(CanWeaveMachineQuery query) {
        var partOrder = weavingOrderRepository.getPartOrderById(new WeavingPartOrderId(query.getWeavingPartOrderId()));
        if (null == partOrder) {
            return List.of();
        }
        var component = styleRepository.getComponentBySkuAndPart(partOrder.getProduceOrderCode(), partOrder.getDemand().getSkuCode(),
                partOrder.getDemand().getPart());
        if (null == component) {
            return List.of();
        }
        var options = resourceDomainService.compatibleMachineByStyleComponent(component);
        var machineList = machineRepository.filterMachineByOption(options, partOrder.getFactoryId());
        return machineList.stream().map(it -> it.getId().value()).toList();
    }

    private BuildProduceDemandContext prepareProduceDemandContext(ProduceOrder produceOrder) {

        var skuDemand = produceOrder.extractDemand();
        var skuList = styleRepository.listStyleSkuByCode(null, skuDemand.skuCodeList(), true);
        var skuAndComponentMap = bomDomainService.rebuildSkuList(skuList);

        return new BuildProduceDemandContext(skuAndComponentMap.skuMap(),
                skuDemand.demandMap(),
                skuAndComponentMap.cylinderComponentMap(),
                skuAndComponentMap.cylinderComponentMap().keySet().stream().sorted().toList());
    }

    private OverviewOrderDTO countOrder(List<ProduceOrder> orderList, List<WeavingOrder> weavingOrderList) {
        var dto = new OverviewOrderDTO();
        dto.setUnFinishedCount(orderList.size());
        dto.setUnPlannedCount((int) orderList.stream()
                .filter(ProduceOrder::unPlanned)
                .count());
        dto.setUnPlannedStyleCount((int) weavingOrderList.stream()
                .filter(WeavingOrder::unPlanned)
                .count());
        return dto;
    }

    private List<WeavingOrderDTO> assembleWeavingOrderDTO(List<WeavingOrder> weavingOrderList) {
        var skuCodeList = weavingOrderList.stream().map(it -> it.getDemand().getSkuCode()).toList();
        var styleMap = styleRepository.listStyleSkuByCode(null, skuCodeList, false)
                .stream().collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v1));
        return weavingOrderList.stream()
                .map(it -> weavingOrderDTOAssembler.convertToWeavingOrderDTO(it, styleMap.get(it.getDemand().getSkuCode().value())))
                .toList();
    }

    private BuildProduceOrderContext prepareComposeProduceOrderContext(List<ProduceOrder> orderList) {
        Set<SkuCode> skuCodeSet = Sets.newHashSet();
        List<ProduceOrderId> orderIds = Lists.newArrayListWithExpectedSize(orderList.size());

        for (var order : orderList) {
            orderIds.add(order.getId());
            skuCodeSet.addAll(order.getDemands().stream().map(StyleDemand::getSkuCode).toList());
        }

        var styleMap = styleRepository.listStyleSkuByCode(null, skuCodeSet.stream().toList(), false)
                .stream().collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v1));
        var factoryMap = organizationRepository.listAllFactory()
                .stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it));
        var taskMap = factoryPlanRepository.listByOrderIds(orderIds)
                .stream().collect(Collectors.toMap(it -> it.getProduceOrderId().value(), it -> it, (v1, v2) -> v1));

        return new BuildProduceOrderContext(styleMap, factoryMap, taskMap);

    }

    private BuildProduceOrderContext prepareComposeProduceOrderContext(ProduceOrder order) {

        var styleMap = styleRepository.listStyleSkuByCode(null, order.getDemands().stream().map(StyleDemand::getSkuCode).toList(), false)
                .stream().collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v1));

        return new BuildProduceOrderContext(styleMap, Collections.emptyMap(), Collections.emptyMap());

    }
}
