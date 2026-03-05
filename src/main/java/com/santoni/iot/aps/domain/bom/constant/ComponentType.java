package com.santoni.iot.aps.domain.bom.constant;

import lombok.Getter;

@Getter
public enum ComponentType {

    PRINCIPAL(0, "正件"),
    ACCESSORY(1, "配件"),
    ;

    private int code;
    private String desc;

    ComponentType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ComponentType getByCode(int code) {
        for (var type : ComponentType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的部位类型:" + code);
    }
}
