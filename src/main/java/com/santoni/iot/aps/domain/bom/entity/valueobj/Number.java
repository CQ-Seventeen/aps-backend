package com.santoni.iot.aps.domain.bom.entity.valueobj;


public record Number(int value) {

    public Number {
        if (value < 1) {
            throw new IllegalArgumentException("部位数不可小于1");
        }
    }

}
