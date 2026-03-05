package com.santoni.iot.aps.domain.support.entity.valueobj;

import lombok.Getter;

@Getter
public enum OptionType {

    SINGLE(0, "单选"),
    MULTIPLE(1, "多选"),
    ;

    private int code;
    private String value;

    OptionType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static OptionType findByCode(int code) {
        for (var type : OptionType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的选项类型");
    }
}
