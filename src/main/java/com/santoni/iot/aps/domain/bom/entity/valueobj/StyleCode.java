package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record StyleCode(String value) {

    public StyleCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("款式编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StyleCode styleCode)) return false;
        return StringUtils.equals(value, styleCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
