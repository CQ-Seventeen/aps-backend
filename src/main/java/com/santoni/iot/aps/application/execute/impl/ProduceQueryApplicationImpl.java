package com.santoni.iot.aps.application.execute.impl;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.execute.ProduceQueryApplication;
import com.santoni.iot.aps.application.execute.assembler.ExecuteDTOAssembler;
import com.santoni.iot.aps.application.execute.assembler.ProduceAssembler;
import com.santoni.iot.aps.application.execute.dto.OrderStyleProductionDTO;
import com.santoni.iot.aps.application.execute.dto.ProductionAggregateByMachineDTO;
import com.santoni.iot.aps.application.execute.dto.StyleComponentProductionDTO;
import com.santoni.iot.aps.application.execute.query.FactoryProductionTrackQuery;
import com.santoni.iot.aps.application.execute.query.OrderStyleProductionQuery;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.MachineAggregateTable;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.execute.repository.ManuallyReportExecuteRepository;
import com.santoni.iot.aps.domain.execute.service.ExecuteDomainService;
import com.santoni.iot.aps.domain.execute.util.SumKeyUtil;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.plan.repository.FactoryPlanRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProduceQueryApplicationImpl implements ProduceQueryApplication {

    @Autowired
    private ExecuteDomainService executeDomainService;

    @Autowired
    private ManuallyReportExecuteRepository manuallyReportExecuteRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private FactoryPlanRepository factoryPlanRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ProduceAssembler produceAssembler;

    @Autowired
    private ExecuteDTOAssembler executeDTOAssembler;

    @Override
    public List<StyleComponentProductionDTO> queryDailyProductionTrack(FactoryProductionTrackQuery query) {
        var taskList = factoryPlanRepository.listUnfinishedTaskByFactory(new FactoryId(query.getFactoryId()));
        if (CollectionUtils.isEmpty(taskList)) {
            return Collections.emptyList();
        }
        var produceOrderIds = taskList.stream().map(FactoryTask::getProduceOrderId).toList();
        var produceOrderList = produceOrderRepository.listProduceOrderById(produceOrderIds);
        var partOrderPair = weavingOrderRepository.listPartOrderByProduceOrderIds(produceOrderIds)
                .stream().collect(Collectors.teeing(
                        Collectors.mapping(it -> it.getDemand().getSkuCode(), Collectors.toSet()),
                        Collectors.groupingBy(it -> it.getProduceOrderId().value()),
                        Pair::of
                ));
        var date = new ProduceDate(query.getDate());
        var productionList = getLatestProductionSum(taskList.get(0).getFactoryId(),
                produceOrderList, partOrderPair.getRight(), date);
        var skuMap = styleRepository.listStyleSkuByCode(null, partOrderPair.getLeft().stream().toList(), false)
                .stream().collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v1));
        return produceAssembler.assembleStyleComponentProductionDTOList(produceOrderList, partOrderPair.getRight(), productionList, skuMap, date);
    }

    @Override
    public List<OrderStyleProductionDTO> queryOrderStyleProduction(OrderStyleProductionQuery query) {
        List<FactoryTask> taskList;
        if (null != query.getFactoryId()) {
            taskList = factoryPlanRepository.listUnfinishedTaskByFactory(new FactoryId(query.getFactoryId()));
        } else {
            taskList = factoryPlanRepository.listUnfinishedTask();
        }
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        var produceOrderIds = taskList.stream().map(FactoryTask::getProduceOrderId).toList();
        var produceOrderList = produceOrderRepository.listProduceOrderById(produceOrderIds);
        var skuMap = styleRepository.listStyleSkuByCode(null, produceOrderList
                        .stream()
                        .flatMap(it -> it.getDemands().stream())
                        .map(StyleDemand::getSkuCode).distinct().toList(), false)
                .stream().collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v2));
        var productionList = getLatestProductionSum(produceOrderList);

        var firstRecord = manuallyReportExecuteRepository.listFirstProductionOnSku(produceOrderList.stream().map(ProduceOrder::getCode).toList())
                .stream()
                .collect(Collectors.groupingBy(it -> it.getOrderCode().value(),
                        Collectors.toMap(it -> it.getSkuCode().value(), it -> it, (v1, v2) -> v1)));
        return produceAssembler.assembleOrderStyleProductionDTOList(produceOrderList, skuMap, productionList, firstRecord);
    }

    @Override
    public List<ProductionAggregateByMachineDTO> queryProductionAggregateByMachine() {
        var allMachine = machineRepository.listAllMachine();

        var table = new MachineAggregateTable();
        table.collectFromMachineList(allMachine);

        var unfinishOrder = produceOrderRepository.unfinishedProduceOrder();
        if (CollectionUtils.isEmpty(unfinishOrder)) {
            return buildProductionAggregateList(table);
        }
        var orderTagPair = unfinishOrder.stream()
                .collect(Collectors.teeing(
                        Collectors.mapping(ProduceOrder::getId, Collectors.toList()),
                        Collectors.mapping(ProduceOrder::getCode, Collectors.toList()),
                        Pair::of
                ));
        var partOrderMap = weavingOrderRepository.listPartOrderByProduceOrderIds(orderTagPair.getLeft())
                .stream().collect(Collectors.groupingBy(it -> it.getProduceOrderId().value()));
        var skuMap = styleRepository.listStyleSkuByOrderCode(orderTagPair.getRight(), true)
                .stream().collect(Collectors.groupingBy(it -> it.getProduceOrderCode().value()));

        for (var order : unfinishOrder) {
            var partOrders = partOrderMap.getOrDefault(order.getId().value(), List.of());
            var skuList = skuMap.getOrDefault(order.getCode().value(), List.of());

            var sumList = getLatestProductionSumByPart(partOrders);
            executeDomainService.collectProductionByOrder(table, partOrders, sumList, skuList);
        }
        return buildProductionAggregateList(table);
    }

    private List<ProductionAggregateByMachineDTO> buildProductionAggregateList(MachineAggregateTable table) {
        var aggregateList = table.listAll();
        return aggregateList.stream().map(it -> executeDTOAssembler.assembleProductionAggregateByMachineDTO(it))
                .sorted(Comparator
                        .comparing(ProductionAggregateByMachineDTO::getNeedleSpacing)
                        .thenComparing(ProductionAggregateByMachineDTO::getCylinderDiameter)).collect(Collectors.toList());
    }

    private List<ProductionSum> getLatestProductionSumByPart(List<WeavingPartOrder> partOrders) {
        if (CollectionUtils.isEmpty(partOrders)) {
            return List.of();
        }
        List<SumKey> keyList = partOrders.stream().map(it -> new SumKey(it.getSumKey())).toList();
        return manuallyReportExecuteRepository.listLatestProductionSumByKey(keyList, SumKeyType.PART);
    }

    private List<ProductionSum> getLatestProductionSum(List<ProduceOrder> produceOrderList) {
        List<SumKey> keyList = Lists.newArrayList();
        for (var order : produceOrderList) {
            keyList.addAll(order.getDemands().stream().map(it ->
                    new SumKey(SumKeyUtil.buildSkuLevelKey(order.getCode(), it.getSkuCode()))).toList());
        }
        return manuallyReportExecuteRepository.listLatestProductionSumByKey(keyList, SumKeyType.SKU);
    }

    private List<ProductionSum> getLatestProductionSum(FactoryId factoryId,
                                                       List<ProduceOrder> produceOrderList,
                                                       Map<Long, List<WeavingPartOrder>> partOrderMap,
                                                       ProduceDate produceDate) {
        List<SumKey> keyList = Lists.newArrayList();
        for (var order : produceOrderList) {
            var partOrders = partOrderMap.get(order.getId().value());
            if (CollectionUtils.isEmpty(partOrders)) {
                continue;
            }
            keyList.addAll(partOrders.stream().map(it ->
                    new SumKey(SumKeyUtil.buildComponentLevelKey(order.getCode(), it.getDemand().getSkuCode(), it.getDemand().getPart()))).toList());
        }
        return produceDate.isToday() ? manuallyReportExecuteRepository.listFactoryLatestProductionSumByKey(factoryId, keyList, SumKeyType.PART) :
                manuallyReportExecuteRepository.listProductionSumByDate(produceDate, factoryId, SumKeyType.PART);
    }
}
