package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record LotNumber(String value) {

    public LotNumber {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("批次号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LotNumber lotNumber)) return false;
        return StringUtils.equals(value, lotNumber.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
