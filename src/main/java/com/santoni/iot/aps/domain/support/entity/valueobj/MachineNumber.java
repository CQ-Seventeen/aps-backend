package com.santoni.iot.aps.domain.support.entity.valueobj;


import java.util.Objects;

public record MachineNumber(int value) {

    public MachineNumber {
        if (value < 0) {
            throw new IllegalArgumentException("机器数不可小于0");
        }
    }

    public static MachineNumber zero() {
        return new MachineNumber(0);
    }

    public boolean isZero() {
        return value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineNumber machineNumber)) return false;
        return value == machineNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
