package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Twist(String value) {

    public Twist {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("捻向不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Twist twist)) return false;
        return StringUtils.equals(value, twist.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
