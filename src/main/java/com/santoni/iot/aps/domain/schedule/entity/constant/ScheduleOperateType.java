package com.santoni.iot.aps.domain.schedule.entity.constant;

import lombok.Getter;

@Getter
public enum ScheduleOperateType {

    STYLE_MACHINE(0, ""),
    MULTI_STYLE_MACHINE(1, ""),
    ;

    private int code;
    private String desc;

    ScheduleOperateType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ScheduleOperateType getByCode(int code) {
        for (var type : ScheduleOperateType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的调度操作类型:" + code);
    }
}
