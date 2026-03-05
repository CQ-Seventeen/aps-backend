package com.santoni.iot.aps.domain.support.entity.organization.valueobj;

import org.apache.commons.lang3.StringUtils;

public record FactoryCode(String value) {

    public FactoryCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("工厂编号不可为空");
        }
    }
}
