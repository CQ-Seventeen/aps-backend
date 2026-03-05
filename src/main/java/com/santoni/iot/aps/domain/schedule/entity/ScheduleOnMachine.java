package com.santoni.iot.aps.domain.schedule.entity;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleOnMachine {

    private MachineId machineId;

    private List<StyleWeavePlan> styleWeavePlanList;

    public ScheduleOnMachine(MachineId machineId, List<StyleWeavePlan> styleWeavePlanList) {
        this.machineId = machineId;
        this.styleWeavePlanList = styleWeavePlanList;
    }
}
