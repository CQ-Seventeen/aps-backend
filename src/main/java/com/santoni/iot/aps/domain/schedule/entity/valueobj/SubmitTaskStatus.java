package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import lombok.Getter;

@Getter
public enum SubmitTaskStatus {

    SUBMITTED(0, "已提交"),
    PROCESSING(1, "执行中"),
    FINISHED(2, "已完成"),
    EXCEPTION(3, "异常"),
    ;

    private int code;
    private String desc;

    SubmitTaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SubmitTaskStatus findByCode(int code) {
        for (var status : SubmitTaskStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的任务状态码:" + code);
    }
}
