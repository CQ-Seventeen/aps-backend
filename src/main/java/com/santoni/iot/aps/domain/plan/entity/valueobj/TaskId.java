package com.santoni.iot.aps.domain.plan.entity.valueobj;

import java.util.Objects;

public record TaskId(long value) {

    public TaskId {
        if (value < 0) {
            throw new IllegalArgumentException("任务id不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskId taskId)) return false;
        return value == taskId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
