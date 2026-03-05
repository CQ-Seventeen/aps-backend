package com.santoni.iot.aps.domain.resource.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

public record MachineGroupCode(String value) {

    public MachineGroupCode {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("机组编号不可为空");
        }
    }
}
