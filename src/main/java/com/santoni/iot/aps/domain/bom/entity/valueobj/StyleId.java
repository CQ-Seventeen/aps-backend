package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record StyleId(long value) {

    public StyleId {
        if (value <= 0) {
            throw new IllegalArgumentException("款式id必须大于0");
        }
    }
}
