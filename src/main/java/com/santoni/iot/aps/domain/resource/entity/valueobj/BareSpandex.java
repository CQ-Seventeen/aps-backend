package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record BareSpandex(String value) {

    public BareSpandex {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("裸氨设备类型不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BareSpandex that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
