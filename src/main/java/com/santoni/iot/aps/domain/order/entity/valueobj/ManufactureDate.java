package com.santoni.iot.aps.domain.order.entity.valueobj;

import java.time.LocalDateTime;

public record ManufactureDate(LocalDateTime value) {

    public ManufactureDate {
        if (null == value) {
            throw new IllegalArgumentException("生产日期不可为空");
        }
    }
}

