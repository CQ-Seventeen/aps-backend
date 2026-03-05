package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import java.time.LocalDateTime;
import java.util.Objects;

public record OperateTime(LocalDateTime value) {

    public OperateTime {
        if (null == value) {
            throw new IllegalArgumentException("操作时间不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperateTime taskId)) return false;
        return value == taskId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
