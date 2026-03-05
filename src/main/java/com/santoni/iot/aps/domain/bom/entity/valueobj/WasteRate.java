package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record WasteRate(double value) {

    public WasteRate {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("废率必须在0-100之间");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WasteRate wasteRate)) return false;
        return Double.compare(value, wasteRate.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

