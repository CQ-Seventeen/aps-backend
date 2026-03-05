package com.santoni.iot.aps.domain.order.entity.valueobj;

public record WeavingOrderId(long value) {

    public WeavingOrderId {
        if (value < 0) {
            throw new IllegalArgumentException("织造单id不可小于0");
        }
    }
}
