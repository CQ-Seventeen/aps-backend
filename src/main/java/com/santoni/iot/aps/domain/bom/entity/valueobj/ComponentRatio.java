package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record ComponentRatio(int value) {

    public ComponentRatio {
        if (value <= 0) {
            throw new IllegalArgumentException("款式部件比率必须大于0");
        }
    }
}
