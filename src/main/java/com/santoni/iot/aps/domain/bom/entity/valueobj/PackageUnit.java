package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record PackageUnit(String value) {

    public PackageUnit {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("包装单位不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageUnit twist)) return false;
        return StringUtils.equals(value, twist.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
