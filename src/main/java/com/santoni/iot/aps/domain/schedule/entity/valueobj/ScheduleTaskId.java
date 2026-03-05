package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import java.util.Objects;

public record ScheduleTaskId(long value) {

    public ScheduleTaskId {
        if (value < 0) {
            throw new IllegalArgumentException("排程任务id不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleTaskId taskId)) return false;
        return value == taskId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
