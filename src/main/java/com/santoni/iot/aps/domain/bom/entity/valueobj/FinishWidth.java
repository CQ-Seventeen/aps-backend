package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record FinishWidth(double value) {

    public FinishWidth {
        if (value < 0) {
            throw new IllegalArgumentException("成品幅宽不可为负数");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinishWidth finishWidth)) return false;
        return Double.compare(value, finishWidth.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

