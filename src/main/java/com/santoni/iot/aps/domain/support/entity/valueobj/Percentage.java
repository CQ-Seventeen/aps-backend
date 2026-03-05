package com.santoni.iot.aps.domain.support.entity.valueobj;

import java.math.BigDecimal;

public record Percentage(BigDecimal value) {

    public Percentage {
        if (null == value || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("百分比必须大于0");
        }
    }
}
