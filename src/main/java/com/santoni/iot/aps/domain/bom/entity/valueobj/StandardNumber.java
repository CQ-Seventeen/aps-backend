package com.santoni.iot.aps.domain.bom.entity.valueobj;


public record StandardNumber(int value) {

    public StandardNumber {
        if (value < 0) {
            throw new IllegalArgumentException("标准框数量不可小于0");
        }
    }
}
