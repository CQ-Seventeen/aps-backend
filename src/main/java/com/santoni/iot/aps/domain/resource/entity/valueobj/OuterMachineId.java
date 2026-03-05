package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record OuterMachineId(String value) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OuterMachineId that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
