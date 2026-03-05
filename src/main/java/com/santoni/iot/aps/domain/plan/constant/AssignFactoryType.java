package com.santoni.iot.aps.domain.plan.constant;

import lombok.Getter;

@Getter
public enum AssignFactoryType {

    ALL(0, ""),
    PART(1, ""),
    ;

    private int code;
    private String desc;

    AssignFactoryType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AssignFactoryType getByCode(int code) {
        for (var type : AssignFactoryType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的分派类型:" + code);
    }
}
