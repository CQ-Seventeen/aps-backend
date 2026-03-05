package com.santoni.iot.aps.domain.schedule.service.impl;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.context.ScheduleContext;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.StyleWeavePlan;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskId;
import com.santoni.iot.aps.domain.schedule.service.ScheduleDomainService;
import com.santoni.iot.aps.domain.schedule.solver.or.entity.WeavingTaskAssign;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.MachineResource;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.ToWeavedStyle;
import com.santoni.iot.aps.domain.schedule.solver.or.solution.ORSolution;
import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScheduleDomainServiceImpl implements ScheduleDomainService {

    @Autowired
    private TimePeriodService timePeriodService;

    @Override
    public ORSolution buildORSolution(ScheduleContext context) {
        var solution = new ORSolution();

        var machineList= buildMachineResourceList(context);
        var toWeaveStyleList = buildToWeavedStyleList(context);
        var initialAssignList = initWeavingAssign(context.task().getTaskId(), machineList, toWeaveStyleList);

        solution.setAssignList(initialAssignList);
        solution.setMachineList(machineList);
        solution.setStyleList(toWeaveStyleList);
        return solution;
    }

    private List<MachineResource> buildMachineResourceList(ScheduleContext context) {
        List<MachineResource> result = Lists.newArrayListWithExpectedSize(context.machinePlanList().size());
        for (var machinePlan : context.machinePlanList()) {
            var canWeaveStyleList = context.machineStylePair().get(machinePlan.getMachine().getId().value());
            if (CollectionUtils.isEmpty(canWeaveStyleList)) {
                continue;
            }
            var machineResource = new MachineResource();
            machineResource.setMachineId(machinePlan.getMachine().getId().value());
            machineResource.setCanWeavingStyles(canWeaveStyleList.stream().map(it -> it.getSkuCode().value()).toList());
            machinePlan.calculateAvailableTimePeriod(timePeriodService, context.timePeriod().getStart().value(),
                    context.timePeriod().getEnd().value());
            machineResource.setAvailableTime(machinePlan.getAvailableTime().getTotalSeconds());
            result.add(machineResource);
        }
        return result;
    }

    private List<ToWeavedStyle> buildToWeavedStyleList(ScheduleContext context) {
        List<ToWeavedStyle> result = Lists.newArrayListWithExpectedSize(context.demandList().size());
        for (var demand : context.demandList()) {
            var toWeavedStyle = new ToWeavedStyle();
            toWeavedStyle.setStyleCode(demand.getSkuCode().value());
            toWeavedStyle.setWeavingOrderId(demand.getWeavingPartOrderId().value());
            // todo
//            toWeavedStyle.setTimePerPiece((int) demand.getSkuCode().getExpectedProduceTime().code());
            toWeavedStyle.setTotalQuantity(demand.getQuantity().getValue());
            result.add(toWeavedStyle);
        }
        return result;
    }

    private List<WeavingTaskAssign> initWeavingAssign(ScheduleTaskId taskId,
                                                      List<MachineResource> machineList,
                                                      List<ToWeavedStyle> styleDemandList) {
        int index = 1;
        var styleDemandMap = styleDemandList.stream().collect(Collectors.groupingBy(ToWeavedStyle::getStyleCode));
        List<WeavingTaskAssign> result = Lists.newArrayList();
        for (var machine : machineList) {
            for (var styleCode : machine.getCanWeavingStyles()) {
                var demandList = styleDemandMap.get(styleCode);
                for (var demand : demandList) {
                    var assign = new WeavingTaskAssign();
                    assign.setId(taskId.value() + "-" + index++);
                    assign.setMachineResource(machine);
                    assign.setStyle(demand);
                    result.add(assign);
                }
            }
        }
        return result;
    }

    @Override
    public AvailableTime calculateAvailableTimePeriod(MachinePlan existPlan, ScheduleOnMachine scheduleOnMachine, TimePeriod timePeriod) {
        var existPeriodStream = existPlan.getTaskList().stream()
                .map(PlannedTask::getSegments)
                .flatMap(List::stream)
                .map(it -> it.getPlan().getPeriod().toTimePeriod());
        var schedulePeriodStream = scheduleOnMachine.getStyleWeavePlanList().stream()
                .map(StyleWeavePlan::getTimePeriod);
        var timePeriods = Stream.concat(existPeriodStream, schedulePeriodStream)
                .sorted(Comparator.comparing(t -> t.getStart().value()))
                .toList();

        return timePeriodService.calculateAvailableTime(timePeriods, timePeriod.getStart().value(), timePeriod.getEnd().value());
    }
}
