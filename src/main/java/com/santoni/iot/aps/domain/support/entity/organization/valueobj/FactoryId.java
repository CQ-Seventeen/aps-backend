package com.santoni.iot.aps.domain.support.entity.organization.valueobj;

public record FactoryId(long value) {
    public FactoryId {
        if (value <= 0) {
            throw new IllegalArgumentException("工厂id必须大于0");
        }
    }
}
