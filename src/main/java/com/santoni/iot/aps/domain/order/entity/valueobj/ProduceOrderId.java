package com.santoni.iot.aps.domain.order.entity.valueobj;

import java.util.Objects;

public record ProduceOrderId(long value) {

    public ProduceOrderId {
        if (value < 0) {
            throw new IllegalArgumentException("生产单id不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProduceOrderId that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
