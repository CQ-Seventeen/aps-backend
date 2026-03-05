package com.santoni.iot.aps.domain.plan.entity;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.bom.entity.MachineRequirement;

import java.util.Map;

public class MachineTimeConsume {

    private Map<MachineRequirement, Long> table;

    private long totalSeconds;

    public MachineTimeConsume() {
        this.table = Maps.newHashMap();
        this.totalSeconds = 0;
    }

    public void count(MachineRequirement requirement, long seconds) {
        var current = table.get(requirement);
        if (null == current) {
            table.put(requirement, seconds);
        } else {
            current += seconds;
            table.put(requirement, current);
        }
        totalSeconds += seconds;
    }
}
