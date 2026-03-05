package com.santoni.iot.aps.domain.plan.service;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.MachineTimeConsume;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface PlanDomainService {

    void sortMachinePlanListByStartTime(List<MachinePlan> machineList);

    void sortMachinePlanByCylinderAndStartTime(List<MachinePlan> machineList);

    MachineTimeConsume calculateOrderConsume(ProduceOrder produceOrder, Map<String, StyleSku> styleSkuMap);

    List<MachinePlan> composeMachinePlanList(List<Machine> machineList,
                                             Map<Long, List<PlannedTask>> taskMap,
                                             LocalDateTime startTime,
                                             LocalDateTime endTime);

    void sortMachinePlanListByAvailableTime(List<MachinePlan> machineList);

    void sortMachinePlanListByEndTime(List<MachinePlan> machineList);

}
