package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Dye(String value) {

    public Dye {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("染料不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dye dye)) return false;
        return StringUtils.equals(value, dye.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

