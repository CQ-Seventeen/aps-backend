package com.santoni.iot.aps.domain.plan.entity.valueobj;

public record TaskSegmentId(long value) {

    public TaskSegmentId {
        if (value < 0) {
            throw new IllegalArgumentException("任务分段id不可小于0");
        }
    }
}
