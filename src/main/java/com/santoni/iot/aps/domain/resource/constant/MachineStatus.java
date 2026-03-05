package com.santoni.iot.aps.domain.resource.constant;

import lombok.Getter;

@Getter
public enum MachineStatus {

    SHUT_DOWN(0, "关机"),
    RUNNING(1, "运行中"),
    IDLE(2, "空闲"),
    STOP(3, "停机"),
    ;

    private int code;
    private String desc;

    MachineStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MachineStatus getByCode(int code) {
        for (MachineStatus status : MachineStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("不支持的机器状态码: " + code);
    }
}
