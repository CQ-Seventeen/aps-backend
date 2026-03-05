package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record FinishLength(double value) {

    public FinishLength {
        if (value < 0) {
            throw new IllegalArgumentException("成品长度不可为负数");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinishLength finishLength)) return false;
        return Double.compare(value, finishLength.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

