package com.santoni.iot.aps.domain.support.entity.valueobj;

import java.time.LocalDateTime;

public record StartTime(LocalDateTime value) {

    public StartTime {
        if (null == value) {
            throw new IllegalArgumentException("开始时间不得为空");
        }
    }
}
