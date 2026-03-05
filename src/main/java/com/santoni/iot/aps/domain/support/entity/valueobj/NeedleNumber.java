package com.santoni.iot.aps.domain.support.entity.valueobj;


import java.util.Objects;

public record NeedleNumber(int value) {

    public NeedleNumber {
        if (value < 0) {
            throw new IllegalArgumentException("针数不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeedleNumber needleNumber)) return false;
        return value == needleNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
