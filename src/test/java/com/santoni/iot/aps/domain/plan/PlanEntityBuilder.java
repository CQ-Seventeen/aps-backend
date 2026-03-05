package com.santoni.iot.aps.domain.plan;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.resource.MachineEntityBuilder;

public class PlanEntityBuilder {

    public static MachinePlan getMachinePlan() {
        return MachinePlan.of(MachineEntityBuilder.getMachine(),
                Lists.newArrayList(TaskEntityBuilder.getPlannedTaskWithSegment()));
    }
}
