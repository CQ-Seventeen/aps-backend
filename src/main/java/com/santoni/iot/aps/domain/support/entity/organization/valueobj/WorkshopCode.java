package com.santoni.iot.aps.domain.support.entity.organization.valueobj;

import org.apache.commons.lang3.StringUtils;

public record WorkshopCode(String value) {

    public WorkshopCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("车间编号不可为空");
        }
    }
}
