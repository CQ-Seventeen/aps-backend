package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record ExpectedProduceTime(double value) {

    public ExpectedProduceTime {
        if (Double.compare(value, 0.0) < 0) {
            throw new IllegalArgumentException("预计下机时间必须大于0");
        }
    }
}
