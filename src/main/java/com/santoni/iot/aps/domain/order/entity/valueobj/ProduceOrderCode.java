package com.santoni.iot.aps.domain.order.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record ProduceOrderCode(String value) {

    public ProduceOrderCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("生产单编号不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProduceOrderCode orderCode)) return false;
        return StringUtils.equals(value, orderCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
