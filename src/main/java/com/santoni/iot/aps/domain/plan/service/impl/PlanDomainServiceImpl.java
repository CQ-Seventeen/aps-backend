package com.santoni.iot.aps.domain.plan.service.impl;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.MachineTimeConsume;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.service.PlanDomainService;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlanDomainServiceImpl implements PlanDomainService {

    @Autowired
    private TimePeriodService timePeriodService;

    @Override
    public void sortMachinePlanListByStartTime(List<MachinePlan> machineList) {
        machineList.sort(this::compareMachinePlanByStartTime);
    }

    @Override
    public void sortMachinePlanByCylinderAndStartTime(List<MachinePlan> machineList) {
        machineList.sort(this::compareMachinePlanByCylinderAndStartTime);
    }

    @Override
    public MachineTimeConsume calculateOrderConsume(ProduceOrder produceOrder, Map<String, StyleSku> styleSkuMap) {
        var res = new MachineTimeConsume();
        for (var demand : produceOrder.getDemands()) {
            accumulateConsume(res, demand, styleSkuMap);
        }
        return res;
    }

    @Override
    public List<MachinePlan> composeMachinePlanList(List<Machine> machineList,
                                                    Map<Long, List<PlannedTask>> taskMap,
                                                    LocalDateTime startTime,
                                                    LocalDateTime endTime) {

        return machineList.stream().map(it -> {
            var machinePlan = MachinePlan.of(it, taskMap.get(it.getId().value()));
            machinePlan.calculateAvailableTimePeriod(timePeriodService, startTime, endTime);
            return machinePlan;
        }).collect(Collectors.toList());
    }

    @Override
    public void sortMachinePlanListByAvailableTime(List<MachinePlan> machineList) {
        machineList.sort(this::compareMachinePlanByAvailableTime);
    }

    @Override
    public void sortMachinePlanListByEndTime(List<MachinePlan> machineList) {
        machineList.sort(this::compareMachinePlanByEndTime);
    }

    private void accumulateConsume(MachineTimeConsume consume, StyleDemand demand, Map<String, StyleSku> styleSkuMap) {
        var style = styleSkuMap.get(demand.getSkuCode().value());
        if (null == style) {
            return;
        }
        for (var component : style.getComponents()) {
            var totalTime = (component.getExpectedProduceTime().value() * component.getNumber().value()) *
                    demand.getOrderQuantity().getValue() / component.getRatio().value();
            consume.count(component.getRequirement(), (long) totalTime);
        }
    }

    private int compareMachinePlanByCylinderAndStartTime(MachinePlan m1, MachinePlan m2) {
        if (m1.getMachine().getMachineSize().getCylinderDiameter().value()
                != m2.getMachine().getMachineSize().getCylinderDiameter().value()) {
            return m1.getMachine().getMachineSize().getCylinderDiameter().value() < m2.getMachine().getMachineSize().getCylinderDiameter().value() ? -1 : 1;
        }
        if (m1.getMachine().getMachineSize().getNeedleSpacing().value()
                != m2.getMachine().getMachineSize().getNeedleSpacing().value()) {
            return m1.getMachine().getMachineSize().getNeedleSpacing().value() < m2.getMachine().getMachineSize().getNeedleSpacing().value() ? -1 : 1;
        }
        return compareMachinePlanByStartTime(m1, m2);
    }

    private int compareMachinePlanByStartTime(MachinePlan m1, MachinePlan m2) {
        if (CollectionUtils.isEmpty(m1.getTaskList()) && CollectionUtils.isEmpty(m2.getTaskList())) {
            return 0;
        }
        if (CollectionUtils.isEmpty(m1.getTaskList())) {
            return 1;
        }
        if (CollectionUtils.isEmpty(m2.getTaskList())) {
            return -1;
        }
        return m1.getTaskList().get(0).getPlan().compareByStart(m2.getTaskList().get(0).getPlan());
    }

    private int compareMachinePlanByAvailableTime(MachinePlan m1, MachinePlan m2) {
        if (CollectionUtils.isEmpty(m1.getTaskList()) && CollectionUtils.isEmpty(m2.getTaskList())) {
            return 0;
        }
        if (CollectionUtils.isEmpty(m1.getTaskList())) {
            return -1;
        }
        if (CollectionUtils.isEmpty(m2.getTaskList())) {
            return 1;
        }
        return m1.compareByAvailableTime(m2);
    }


    private int compareMachinePlanByEndTime(MachinePlan m1, MachinePlan m2) {
        if (CollectionUtils.isEmpty(m1.getTaskList()) && CollectionUtils.isEmpty(m2.getTaskList())) {
            return 0;
        }
        if (CollectionUtils.isEmpty(m1.getTaskList())) {
            return -1;
        }
        if (CollectionUtils.isEmpty(m2.getTaskList())) {
            return 1;
        }
        return m1.getTaskList().get(0).getPlan().compareByEnd(m2.getTaskList().get(0).getPlan());
    }
}
