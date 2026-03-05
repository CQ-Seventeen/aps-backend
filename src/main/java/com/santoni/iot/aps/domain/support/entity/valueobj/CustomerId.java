package com.santoni.iot.aps.domain.support.entity.valueobj;

public record CustomerId(long value) {

    public CustomerId {
        if (value <= 0) {
            throw new IllegalArgumentException("客户id必须大于0");
        }
    }
}
