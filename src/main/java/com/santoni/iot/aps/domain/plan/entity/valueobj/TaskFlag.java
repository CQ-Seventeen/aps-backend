package com.santoni.iot.aps.domain.plan.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record TaskFlag(String value) {

    public TaskFlag {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("任务标记不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskFlag taskFlag)) return false;
        return StringUtils.equals(value, taskFlag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

