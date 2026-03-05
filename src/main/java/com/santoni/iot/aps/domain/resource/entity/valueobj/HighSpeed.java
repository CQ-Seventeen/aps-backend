package com.santoni.iot.aps.domain.resource.entity.valueobj;

import java.util.Objects;

public record HighSpeed(boolean value) {

    public static HighSpeed low() {
        return new HighSpeed(false);
    }

    public static HighSpeed high() {
        return new HighSpeed(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HighSpeed highSpeed)) return false;
        return value == highSpeed.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value ? "高速" : "非高速";
    }
}
