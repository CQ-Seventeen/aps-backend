package com.santoni.iot.aps.domain.schedule.solver.greedy.solver;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.StyleWeavePlan;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.context.ScheduleContext;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.GreedySolution;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.ArrangeMidResult;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.machine.MachineAvailabilitySnapshot;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.machine.MachineAvailabilityTable;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.StyleRequirement;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.StyleRequirementTable;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.resource.AvailableResourceTable;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GreedySolver {

    @Autowired
    private TimePeriodService timePeriodService;

    public GreedySolution solve(ScheduleContext context) {
        var requireTable = initStyleRequirementTable(context.demandList(), context.styleComponentMap());
        var resourceTable = initMachineResourceTable(context.timePeriod().getStart().value(),
                requireTable.getAllDeliveryTimes(), context.machinePlanList());

        var midResults = preArrangeResource(resourceTable.getLeft(), requireTable);
        postArrangeResource(midResults, resourceTable.getLeft());
        if (CollectionUtils.isEmpty(midResults)) {
            return null;
        }
        return new GreedySolution(buildAssignPlan(midResults, resourceTable.getRight()));
    }

    private StyleRequirementTable initStyleRequirementTable(List<StylePartWeaveDemand> demandList,
                                                            Map<String, Map<String, Map<String, StyleComponent>>> styleComponentMap) {
        var demandMap = demandList.stream()
                .collect(Collectors.groupingBy(it -> it.getTimePeriod().getEnd().value()));
        StyleRequirementTable table = new StyleRequirementTable();
        for (var entry : demandMap.entrySet()) {
            List<StyleRequirement> requirements = Lists.newArrayListWithExpectedSize(entry.getValue().size());
            for (var demand : entry.getValue()) {
                var component = styleComponentMap.getOrDefault(demand.getOrderCode().value(), Map.of())
                        .getOrDefault(demand.getSkuCode().value(), Map.of())
                        .get(demand.getPart().value());
                if (null == component) {
                    continue;
                }
                requirements.add(new StyleRequirement(demand.getWeavingPartOrderId().value(),
                        component,
                        demand.getQuantity().getValue(),
                        demand.getTimePeriod().getEnd().value()));
            }
            table.collectRequirements(entry.getKey(), requirements);
        }
        return table;
    }

    private Pair<AvailableResourceTable, MachineAvailabilityTable> initMachineResourceTable(LocalDateTime startTime,
                                                                                            List<LocalDateTime> sortedEndTimeNodes,
                                                                                            List<MachinePlan> planList) {
        var table = new AvailableResourceTable();
        var machineTable = new MachineAvailabilityTable();
        for (var plan : planList) {
            var availableTimeList = plan.calculateAvailableTimeBySegment(timePeriodService, startTime, sortedEndTimeNodes);
            var machineSize = plan.getMachine().getMachineSize();
            for (int i = 0; i < availableTimeList.size(); i++) {
                table.accumulateTime(sortedEndTimeNodes.get(i), machineSize.getCylinderDiameter().value(),
                        machineSize.getNeedleSpacing().value(),
                        plan.getMachine().isBareSpandexMachine(), Double.valueOf(availableTimeList.get(i)));

            }
            plan.calculateAvailableTimePeriod(timePeriodService, startTime, sortedEndTimeNodes.get(sortedEndTimeNodes.size() - 1));
            var snapshot = new MachineAvailabilitySnapshot(plan.getMachine().getId().value(),
                    plan.getAvailableTime().getAvailablePeriod(),
                    availableTimeList);
            machineTable.collectMachineAvailability(plan.getMachine(), snapshot);
        }
        return Pair.of(table, machineTable);
    }

    private List<ArrangeMidResult> preArrangeResource(AvailableResourceTable resourceTable, StyleRequirementTable requirementTable) {
        var allTimeNodes = requirementTable.getAllDeliveryTimes();
        List<ArrangeMidResult> midResults = Lists.newArrayListWithExpectedSize(allTimeNodes.size());
        for (var timeNode : allTimeNodes) {
            var requireCol = requirementTable.getCol(timeNode);
            if (requireCol == null) {
                continue;
            }
            var midRes = resourceTable.arrangeRequire(requireCol);
            midResults.add(midRes);
        }
        return midResults;
    }

    private void postArrangeResource(List<ArrangeMidResult> preResult, AvailableResourceTable resourceTable) {
        if (preResult.stream().allMatch(ArrangeMidResult::isSatisfied)) {
            return;
        }
        for (var arrange : preResult) {
            if (arrange.isSatisfied()) {
                continue;
            }
            resourceTable.arrangeLeftTime(arrange);
        }
    }

    private List<ScheduleOnMachine> buildAssignPlan(List<ArrangeMidResult> midResults,
                                                    MachineAvailabilityTable machineTable) {
        List<ScheduleOnMachine> assignList = Lists.newArrayList();
        for (int i = 0; i < midResults.size(); i++) {
            assignList.addAll(machineTable.assign(midResults.get(i), i));
        }
        var assignMap = assignList.stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));
        List<ScheduleOnMachine> res = Lists.newArrayList();
        for (var entry : assignMap.entrySet()) {
            res.add(mergeSchedule(entry.getValue()));
        }
        return res;
    }

    private ScheduleOnMachine mergeSchedule(List<ScheduleOnMachine> scheduleList) {
        List<StyleWeavePlan> planList = Lists.newArrayList();
        for (var schedule : scheduleList) {
            planList.addAll(schedule.getStyleWeavePlanList());
        }
        return new ScheduleOnMachine(scheduleList.get(0).getMachineId(), planList);
    }

}
