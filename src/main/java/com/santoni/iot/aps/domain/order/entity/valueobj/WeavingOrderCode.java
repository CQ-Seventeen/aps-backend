package com.santoni.iot.aps.domain.order.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

public record WeavingOrderCode(String value) {

    public WeavingOrderCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("织造单编号不可为空");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WeavingOrderCode orderCode)) {
            return false;
        }
        return StringUtils.equals(value, orderCode.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
