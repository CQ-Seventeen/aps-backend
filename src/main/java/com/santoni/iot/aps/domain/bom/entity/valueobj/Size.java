package com.santoni.iot.aps.domain.bom.entity.valueobj;

public record Size(String id, String value) {

    public Size(String value) {
        this("", value);
    }
}
