package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record SkuCode(String value) {

    public SkuCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("SKU编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkuCode skuCode)) return false;
        return StringUtils.equals(value, skuCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
