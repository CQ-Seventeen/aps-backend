package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

public record MachineCode(String value) {

    public MachineCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("机器编号不可为空");
        }
    }
}
