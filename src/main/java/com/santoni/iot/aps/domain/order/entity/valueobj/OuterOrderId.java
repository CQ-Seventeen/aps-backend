package com.santoni.iot.aps.domain.order.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record OuterOrderId(String value) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OuterOrderId that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
