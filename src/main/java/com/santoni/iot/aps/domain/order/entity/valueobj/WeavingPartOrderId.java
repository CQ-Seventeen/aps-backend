package com.santoni.iot.aps.domain.order.entity.valueobj;

public record WeavingPartOrderId(long value) {

    public WeavingPartOrderId {
        if (value < 0) {
            throw new IllegalArgumentException("织造部件单id不可小于0");
        }
    }
}

