package com.santoni.iot.aps.domain.order.entity.valueobj;

public record Figure(String value) {

    public Figure {
        if (null == value || value.isBlank()) {
            throw new IllegalArgumentException("图形不可为空");
        }
    }
}

