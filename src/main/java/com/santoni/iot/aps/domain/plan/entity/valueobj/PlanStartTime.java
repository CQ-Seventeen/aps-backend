package com.santoni.iot.aps.domain.plan.entity.valueobj;

import java.time.LocalDateTime;

public record PlanStartTime(LocalDateTime value) {

    public PlanStartTime {
        if (null == value) {
            throw new IllegalArgumentException("计划开始时间不得为空");
        }
    }
}
