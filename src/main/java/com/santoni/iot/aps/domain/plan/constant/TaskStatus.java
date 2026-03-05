package com.santoni.iot.aps.domain.plan.constant;

import lombok.Getter;

@Getter
public enum TaskStatus {

    INIT(0, "初始化"),
    PRODUCING(1, "生产中"),
    SUSPEND(2, "中止"),
    FINISH(3, "完成"),
    CANCEL(4, "取消")
    ;

    private int code;

    private String desc;

    TaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskStatus getByCode(int code) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getCode() == code) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("不支持的任务状态: " + code);
    }

}
