package com.santoni.iot.aps.domain.plan.constant;

import lombok.Getter;

@Getter
public enum FactoryTaskStatus {

    INIT(0, "初始化"),
    PRODUCING(1, "生产中"),
    SUSPEND(2, "中止"),
    FINISH(3, "完成"),
    ;

    private int code;

    private String desc;

    FactoryTaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FactoryTaskStatus getByCode(int code) {
        for (FactoryTaskStatus taskStatus : FactoryTaskStatus.values()) {
            if (taskStatus.getCode() == code) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("不支持的任务状态: " + code);
    }

}
