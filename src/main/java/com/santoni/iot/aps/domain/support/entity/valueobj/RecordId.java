package com.santoni.iot.aps.domain.support.entity.valueobj;

public record RecordId(long value) {

    public RecordId {
        if (value <= 0) {
            throw new IllegalArgumentException("记录id必须大于0");
        }
    }
}
