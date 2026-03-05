package com.santoni.iot.aps.domain.support.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Color(String id, String value) {

    public Color {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("颜色不可为空");
        }
    }

    public Color(String value) {
        this("", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
