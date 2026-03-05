package com.santoni.iot.aps.domain.plan.entity.factory;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceResourceDemand;
import com.santoni.iot.aps.domain.plan.constant.AssignFactoryType;
import com.santoni.iot.aps.domain.plan.constant.FactoryTaskStatus;
import com.santoni.iot.aps.domain.plan.entity.valueobj.OccupiedDays;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineDays;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineNumber;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class FactoryPlan {

    private FactoryId factoryId;

    private Map<Integer, List<Machine>> machineMap;

    private List<FactoryTask> taskList;

    private FactoryPlan(FactoryId factoryId, Map<Integer, List<Machine>> machineMap, List<FactoryTask> taskList) {
        this.factoryId = factoryId;
        this.machineMap = machineMap;
        this.taskList = taskList;
    }

    public static FactoryPlan of(FactoryId factoryId, Map<Integer, List<Machine>> machineMap, List<FactoryTask> taskList) {
        return new FactoryPlan(factoryId, machineMap, taskList);
    }

    public FactoryTask arrangeProduceOrder(ProduceResourceDemand orderDemand) {
        var detailMap = taskList.stream()
                .flatMap(it -> it.getAssignDetail().stream())
                .collect(Collectors.groupingBy(it -> it.getCylinderDiameter().value(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                l -> l.stream()
                                        .sorted((t1, t2) -> t2.getPlanPeriod().getEnd().value().compareTo(t1.getPlanPeriod().getEnd().value())) // 对 code 列表排序
                                        .toList()
                        )));
        List<FactoryTaskDetail> detailList = Lists.newArrayList();
        for (var cylinder : orderDemand.getAllCylinders()) {
            var existTask = detailMap.get(cylinder.value());
            var startTime = CollectionUtils.isEmpty(existTask) ? TimeUtil.getStartOfTomorrow() : existTask.get(0).getPlanPeriod().getEnd().value();
            var detail = defineTaskDetail(cylinder, orderDemand.getByCylinder(cylinder), startTime);
            detailList.add(detail);
        }
        return new FactoryTask(null, orderDemand.getProduceOrder().getId(), orderDemand.getProduceOrder().getCode(),
                factoryId, PlanPeriod.of(LocalDateTime.now(), orderDemand.getProduceOrder().getDeliveryTime().value()), AssignFactoryType.ALL, FactoryTaskStatus.INIT, detailList);

    }

    private FactoryTaskDetail defineTaskDetail(CylinderDiameter cylinderDiameter, MachineDays machineDays, LocalDateTime startTime) {
        var machines = machineMap.get(cylinderDiameter.value());
        if (CollectionUtils.isEmpty(machines)) {
            throw new IllegalArgumentException("尺寸" + cylinderDiameter.value() + ",本工厂没有机器,无法安排订单");
        }
        var openedMachines = (int) machines.stream().filter(it -> it.getStatus() != MachineStatus.SHUT_DOWN).count();
        if (openedMachines == 0) {
            throw new IllegalArgumentException("尺寸" + cylinderDiameter.value() + ",本工厂没有机器开机,无法安排订单");
        }
        var occupiedDays = Math.ceil(machineDays.getDays() * 10 / openedMachines) / 10;
        var endTime = startTime.plusSeconds((long) occupiedDays * 3600 * 24);
        return FactoryTaskDetail.newOf(new MachineNumber(openedMachines), new OccupiedDays(occupiedDays), PlanPeriod.of(startTime, endTime), cylinderDiameter);
    }
}
