package com.santoni.iot.aps.domain.statistic.constant;

import lombok.Getter;

@Getter
public enum WorkLoadLevel {

    LOW(0, "低负载", 0.2),
    MEDIUM(1, "中负载", 0.7),
    HIGH(2, "高负载", 0.9),
    ;

    private int code;
    private String desc;
    private double threshold;

    WorkLoadLevel(int code, String desc, double threshold) {
        this.code = code;
        this.desc = desc;
        this.threshold = threshold;
    }
}
