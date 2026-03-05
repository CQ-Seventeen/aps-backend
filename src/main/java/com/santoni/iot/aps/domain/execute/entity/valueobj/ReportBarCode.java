package com.santoni.iot.aps.domain.execute.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record ReportBarCode(String value) {

    public ReportBarCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("报工条码号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportBarCode orderCode)) return false;
        return StringUtils.equals(value, orderCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
