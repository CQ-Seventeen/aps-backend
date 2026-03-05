package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Yarn(String id, String code) {

    public Yarn {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("纱线编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Yarn yarn)) return false;
        return StringUtils.equals(code, yarn.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
