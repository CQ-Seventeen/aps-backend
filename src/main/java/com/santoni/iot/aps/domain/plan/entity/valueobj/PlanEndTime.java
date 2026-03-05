package com.santoni.iot.aps.domain.plan.entity.valueobj;

import java.time.LocalDateTime;

public record PlanEndTime(LocalDateTime value) {

    public PlanEndTime {
        if (null == value) {
            throw new IllegalArgumentException("计划结束时间不可为空");
        }
    }
}
