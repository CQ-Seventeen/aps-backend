package com.santoni.iot.aps.domain.resource.entity.valueobj;

public record MachineId(long value) {
    public MachineId {
        if (value <= 0) {
            throw new IllegalArgumentException("机器id必须大于0");
        }
    }
}
