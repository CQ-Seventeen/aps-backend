package com.santoni.iot.aps.domain.support.entity.valueobj;

public record InstituteId(long value) {
    public InstituteId {
        if (value <= 0) {
            throw new IllegalArgumentException("企业id必须大于0");
        }
    }
}
