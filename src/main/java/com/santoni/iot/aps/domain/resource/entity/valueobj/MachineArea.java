package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record MachineArea(String value) {

    public MachineArea {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("机器区域不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineArea that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
