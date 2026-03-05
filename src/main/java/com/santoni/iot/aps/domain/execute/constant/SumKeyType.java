package com.santoni.iot.aps.domain.execute.constant;

import lombok.Getter;

@Getter
public enum SumKeyType {

    ORDER(0, "订单"),
    SKU(1, "款式"),
    PART(2, "部件"),
    ;

    private int code;
    private String desc;

    SumKeyType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SumKeyType getByCode(int code) {
        for (var key : SumKeyType.values()) {
            if (key.code == code) {
                return key;
            }
        }
        throw new IllegalArgumentException("暂不支持的聚合键");
    }
}
