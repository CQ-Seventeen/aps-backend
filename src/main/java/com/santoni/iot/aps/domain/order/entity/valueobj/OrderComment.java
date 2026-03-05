package com.santoni.iot.aps.domain.order.entity.valueobj;

public record OrderComment(String value) {

    public OrderComment {
        // 注释可以为空
        if (null == value) {
            throw new IllegalArgumentException("注释值不可为null");
        }
    }
}

