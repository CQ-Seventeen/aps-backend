package com.santoni.iot.aps.domain.support.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Symbol(String id, String value) {

    public Symbol {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("提字色不可为空");
        }
    }

    public Symbol(String value) {
        this("", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
