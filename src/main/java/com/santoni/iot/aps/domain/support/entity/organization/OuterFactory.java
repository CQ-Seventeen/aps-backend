package com.santoni.iot.aps.domain.support.entity.organization;

public record OuterFactory(String id, String code) {

    public OuterFactory(String code) {
        this("", code);
    }
}
