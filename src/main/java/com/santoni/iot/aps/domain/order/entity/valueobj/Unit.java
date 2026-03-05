package com.santoni.iot.aps.domain.order.entity.valueobj;

public record Unit(String value) {

    public Unit {
        if (null == value || value.isBlank()) {
            throw new IllegalArgumentException("单位不可为空");
        }
    }
}

