package com.santoni.iot.aps.domain.support.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record CustomerCode(String value) {

    public CustomerCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("客户编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerCode that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
