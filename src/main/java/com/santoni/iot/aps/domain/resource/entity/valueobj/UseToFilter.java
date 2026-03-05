package com.santoni.iot.aps.domain.resource.entity.valueobj;

public record UseToFilter(boolean value) {
    public static UseToFilter no() {
        return new UseToFilter(false);
    }

    public static UseToFilter yes() {
        return new UseToFilter(true);
    }
}
