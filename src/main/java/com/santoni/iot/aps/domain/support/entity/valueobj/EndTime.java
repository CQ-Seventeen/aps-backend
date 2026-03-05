package com.santoni.iot.aps.domain.support.entity.valueobj;

import java.time.LocalDateTime;

public record EndTime(LocalDateTime value) {

    public EndTime {
        if (null == value) {
            throw new IllegalArgumentException("结束时间不可为空");
        }
    }
}
