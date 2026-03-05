package com.santoni.iot.aps.domain.support.entity.organization.valueobj;

public record WorkshopId(long value) {
    public WorkshopId {
        if (value <= 0) {
            throw new IllegalArgumentException("车间id必须大于0");
        }
    }
}
