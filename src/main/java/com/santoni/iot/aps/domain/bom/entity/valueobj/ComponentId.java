package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record ComponentId(long value) {

    public ComponentId {
        if (value <= 0) {
            throw new IllegalArgumentException("部件id必须大于0");
        }
    }
}
