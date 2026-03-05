package com.santoni.iot.aps.domain.order.entity.valueobj;

public record ManufactureBatch(String value) {

    public ManufactureBatch {
        if (null == value || value.isBlank()) {
            throw new IllegalArgumentException("生产批次不可为空");
        }
    }
}

