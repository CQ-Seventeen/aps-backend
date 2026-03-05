package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record StyleName(String value) {

    public StyleName {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("款式名称不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StyleName name)) return false;
        return StringUtils.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
