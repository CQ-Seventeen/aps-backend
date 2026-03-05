package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;

import java.util.Objects;

public record LogId(long value) {

    public LogId {
        if (value < 0) {
            throw new IllegalArgumentException("调度日志id不可小于0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogId taskId)) return false;
        return value == taskId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public TaskId toTaskId() {
        return new TaskId(value);
    }

    public ScheduleTaskId toScheduleTaskId() {
        return new ScheduleTaskId(value);
    }
}
