package com.santoni.iot.aps.domain.bom.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Part(String id, String value) {

    public Part(String value) {
        this("", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part part)) return false;
        return StringUtils.equals(value, part.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
