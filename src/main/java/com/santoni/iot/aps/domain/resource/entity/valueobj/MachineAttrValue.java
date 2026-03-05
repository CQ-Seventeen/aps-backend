package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record MachineAttrValue(String value) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineAttrValue that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
