package com.santoni.iot.aps.domain.order.entity.valueobj;

public record TaskDetailId(String value) {

    public TaskDetailId {
        if (null == value || value.isBlank()) {
            throw new IllegalArgumentException("任务详情id不可为空");
        }
    }
}

