package com.santoni.iot.aps.domain.resource.entity.valueobj;

public record MachineGroupId(long value) {
    public MachineGroupId {
        if (value <= 0) {
            throw new IllegalArgumentException("机组id必须大于0");
        }
    }
}
