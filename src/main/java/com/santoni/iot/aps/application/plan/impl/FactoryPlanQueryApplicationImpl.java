package com.santoni.iot.aps.application.plan.impl;

import com.santoni.iot.aps.application.order.assembler.ProduceOrderDTOAssembler;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.plan.FactoryPlanQueryApplication;
import com.santoni.iot.aps.application.plan.assembler.FactoryPlanDTOAssembler;
import com.santoni.iot.aps.application.plan.context.BuildFactoryPlanContext;
import com.santoni.iot.aps.application.plan.context.BuildMachineCapacityContext;
import com.santoni.iot.aps.application.plan.dto.factory.FactoryOrderDimPlanDTO;
import com.santoni.iot.aps.application.plan.dto.factory.MachineCapacityDTO;
import com.santoni.iot.aps.application.plan.query.FactoryOrderQuery;
import com.santoni.iot.aps.application.plan.query.FactoryPlanQuery;
import com.santoni.iot.aps.application.plan.query.MachineCapacityQuery;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.plan.repository.FactoryPlanRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FactoryPlanQueryApplicationImpl implements FactoryPlanQueryApplication {

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private FactoryPlanRepository factoryPlanRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FactoryPlanDTOAssembler factoryPlanDTOAssembler;

    @Autowired
    private ProduceOrderDTOAssembler produceOrderDTOAssembler;

    @Override
    public FactoryOrderDimPlanDTO queryFactoryPlanOrderDim(FactoryPlanQuery query) {
        var produceOrder = produceOrderRepository.produceOrderDetailById(new ProduceOrderId(query.getProduceOrderId()));
        if (null == produceOrder) {
            throw new IllegalArgumentException("生产订单不存在:" + query.getProduceOrderId());
        }

        var context = prepareFactoryPlanContext(produceOrder, new FactoryId(query.getFactoryId()),
                new StartTime(query.getStartTime()), new EndTime(query.getEndTime()));

        return factoryPlanDTOAssembler.assembleFactoryOrderDimPlanDTO(query.getStartTime(), query.getEndTime(), produceOrder.getCode(), context);
    }

    @Override
    public List<ProduceOrderDTO> queryOrderInFactory(FactoryOrderQuery query) {
        var taskList = factoryPlanRepository.listUnfinishedTaskByFactory(new FactoryId(query.getFactoryId()));
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        var orderIds = taskList.stream().map(FactoryTask::getProduceOrderId).toList();
        var orderList = produceOrderRepository.listProduceOrderById(orderIds);
        return orderList.stream().map(it -> produceOrderDTOAssembler.convertToProduceOrderDTO(it, null)).toList();
    }

    @Override
    public List<MachineCapacityDTO> queryFactoryMachineCapacity(MachineCapacityQuery query) {
        var factoryList = organizationRepository.listAllFactory();
        if (CollectionUtils.isEmpty(factoryList)) {
            return List.of();
        }
        var context = prepareMachineCapacityContext(factoryList, query);

        return factoryPlanDTOAssembler.assembleMachineCapacityList(query.getStartTime(), query.getEndTime(), context);
    }

    private BuildFactoryPlanContext prepareFactoryPlanContext(ProduceOrder produceOrder, FactoryId factoryId,
                                                              StartTime startTime, EndTime endTime) {

        var skuCodeAndDemandMap = produceOrder.getDemands().stream()
                .collect(Collectors.teeing(
                        Collectors.mapping(StyleDemand::getSkuCode, Collectors.toList()),
                        Collectors.toMap(it -> it.getSkuCode().value(), it -> it, (v1, v2) -> v1),
                        Pair::of
                ));

        var skuList = styleRepository.listStyleSkuByCode(produceOrder.getCode(), skuCodeAndDemandMap.getLeft(), true);
        var componentAndSkuMap = skuList.stream()
                .collect(Collectors.teeing(
                                Collectors.flatMapping(it -> it.getComponents().stream(),
                                        Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value())),
                        Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v2),
                        Pair::of));
        var existTaskList = factoryPlanRepository.findByFactoryId(factoryId, startTime, endTime);
        var machineMap = machineRepository.listMachineByFactory(factoryId)
                .stream().collect(Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value()));

        return new BuildFactoryPlanContext(componentAndSkuMap.getRight(),
                skuCodeAndDemandMap.getRight(),
                componentAndSkuMap.getLeft(),
                componentAndSkuMap.getLeft().keySet().stream().sorted().toList(),
                existTaskList,
                machineMap);
    }

    private BuildMachineCapacityContext prepareMachineCapacityContext(List<Factory> factoryList, MachineCapacityQuery query) {
        var taskMap = factoryPlanRepository.listByFactoryIds(factoryList.stream().map(Factory::getId).toList(),
                new StartTime(query.getStartTime()), new EndTime(query.getEndTime()))
                .stream().collect(Collectors.groupingBy(it -> it.getFactoryId().value(),
                        Collectors.flatMapping(
                                it -> it.getAssignDetail().stream(),
                                Collectors.groupingBy(it -> it.getCylinderDiameter().value()))));
        var machineMap = machineRepository.listAllMachine()
                .stream().collect(Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value(),
                        Collectors.groupingBy(it -> it.getHierarchy().getFactory().getId().value())));

        var cylinderList = machineMap.keySet().stream().sorted().toList();
        return new BuildMachineCapacityContext(factoryList, machineMap, cylinderList, taskMap);
    }
}