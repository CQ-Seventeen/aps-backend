package com.santoni.iot.aps.domain.plan.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record TaskCode(String value) {

    public TaskCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("任务编码不可为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskCode taskCode)) return false;
        return StringUtils.equals(value, taskCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

