package com.santoni.iot.aps.domain.support.entity.valueobj;


import java.util.Objects;

public record NeedleSpacing(int value) {

    public NeedleSpacing {
        if (value < 0) {
            throw new IllegalArgumentException("针距不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeedleSpacing that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
