package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.machine;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TriMap;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.StyleWeavePlan;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.ArrangeMidResult;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.StyleRequirement;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class MachineAvailabilityTable {

    private TriMap<Integer, Integer, Boolean, List<MachineAvailabilitySnapshot>> table = new TriMap<>();

    public void collectMachineAvailability(Machine machine, MachineAvailabilitySnapshot snapshot) {
        var size = machine.getMachineSize();
        var snapshotList = table.get(size.getCylinderDiameter().value(),
                size.getNeedleSpacing().value(),
                machine.isBareSpandexMachine());
        if (CollectionUtils.isNotEmpty(snapshotList)) {
            snapshotList.add(snapshot);
        } else {
            table.put(size.getCylinderDiameter().value(),
                    size.getNeedleSpacing().value(),
                    machine.isBareSpandexMachine(),
                    Lists.newArrayList(snapshot));
        }
    }

    public List<ScheduleOnMachine> assign(ArrangeMidResult midArrange, int index) {
        var styleTable = initStyleTable(midArrange.getRequireList());
        List<ScheduleOnMachine> res = Lists.newArrayList();
        for (var entry : styleTable) {
            var requireList = entry.getValue();
            var machineAvailability = table.get(entry.getKey1(), entry.getKey2(), entry.getKey3());
            var entryRes = doArrange(requireList, midArrange.getEndTimeNode(), machineAvailability, index);
            res.addAll(entryRes);
        }
        return res;
    }

    private TriMap<Integer, Integer, Boolean, List<StyleRequirement>> initStyleTable(List<StyleRequirement> requireList) {
        TriMap<Integer, Integer, Boolean, List<StyleRequirement>> styleTable = new TriMap<>();
        for (var require : requireList) {
            var styleComponent = require.getStyleComponent();
            boolean bareSpandex = null != styleComponent.getRequirement()
                    && !CollectionUtils.isEmpty(styleComponent.getRequirement().getBareSpandexList())
                    && !StringUtils.equals(styleComponent.getRequirement().getBareSpandexList().get(0).value(), "NONE");
            var existList = styleTable.get(styleComponent.getMachineSize().getCylinderDiameter().value(),
                    styleComponent.getMachineSize().getNeedleSpacing().value(),
                    bareSpandex);
            if (CollectionUtils.isNotEmpty(existList)) {
                existList.add(require);
            } else {
                styleTable.put(styleComponent.getMachineSize().getCylinderDiameter().value(),
                        styleComponent.getMachineSize().getNeedleSpacing().value(),
                        bareSpandex,
                        Lists.newArrayList(require));
            }
        }
        return styleTable;
    }

    private List<ScheduleOnMachine> doArrange(List<StyleRequirement> requirementList,
                                              LocalDateTime endTime,
                                              List<MachineAvailabilitySnapshot> machineList,
                                              int index) {
        if (CollectionUtils.isEmpty(machineList)) {
            return List.of();
        }
        machineList.sort((t1, t2) -> {
            long leftTime1 = 0, leftTime2 = 0;
            for (int i = 0; i <= index; i++) {
                leftTime1 += t1.getLeftTimeList().get(i);
                leftTime2 += t2.getLeftTimeList().get(i);
            }
            return (int) (leftTime2 - leftTime1);
        });
        requirementList.sort(Comparator.comparing(StyleRequirement::getQuantity).reversed());
        List<ScheduleOnMachine> res = Lists.newArrayList();
        
        for (var requirement : requirementList) {
            double unitTime = requirement.getStyleComponent().getExpectedProduceTime().value();
            int remainingQuantity = requirement.getQuantity();
            
            for (var machineAvailability : machineList) {
                if (remainingQuantity <= 0) {
                    break;
                }
                var arrangeRes = machineAvailability.arrangeTimeByQuantity(unitTime, remainingQuantity, index, endTime);
                int leftQuantity = arrangeRes.getLeft();        // 剩余未分配数量
                int allocatedQuantity = arrangeRes.getMiddle(); // 实际分配数量
                var timePeriods = arrangeRes.getRight();        // 分配的时间段
                
                if (allocatedQuantity > 0 && !timePeriods.isEmpty()) {
                    var styleWeavePlan = timePeriods.stream().map(timePeriod -> new StyleWeavePlan(
                            new WeavingPartOrderId(requirement.getWeavingPartOrderId()),
                            requirement.getStyleComponent().getProduceOrderCode(),
                            requirement.getStyleComponent().getSkuCode(),
                            requirement.getStyleComponent().getPart(),
                            Quantity.of(allocatedQuantity), // 使用实际分配的数量
                            timePeriod
                    )).toList();
                    
                    res.add(new ScheduleOnMachine(new MachineId(machineAvailability.getMachineId()), styleWeavePlan));
                    
                    requirement.arrangedQuantity(allocatedQuantity);
                }
                remainingQuantity = leftQuantity;
                
                if (remainingQuantity <= 0) {
                    break;
                }
            }
        }
        return res;
    }
}
