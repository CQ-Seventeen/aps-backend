package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record SupplierCode(String value) {

    public SupplierCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("供应商编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierCode supplierCode)) return false;
        return StringUtils.equals(value, supplierCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
