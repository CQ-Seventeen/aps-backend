package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record ExpectedWeight(double value) {

    public ExpectedWeight {
        if (Double.compare(value, 0.0) < 0) {
            throw new IllegalArgumentException("预计下机克重必须大于0");
        }
    }
}
