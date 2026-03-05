package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record YarnName(String value) {

    public YarnName {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("纱线名称不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YarnName yarn)) return false;
        return StringUtils.equals(value, yarn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
