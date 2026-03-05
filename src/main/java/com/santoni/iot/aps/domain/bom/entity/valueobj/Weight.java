package com.santoni.iot.aps.domain.bom.entity.valueobj;

import java.math.BigDecimal;

public record Weight(BigDecimal value) {

    public Weight {
        if (BigDecimal.ZERO.compareTo(value) < 0) {
            throw new IllegalArgumentException("克重必须大于0");
        }
    }
}
