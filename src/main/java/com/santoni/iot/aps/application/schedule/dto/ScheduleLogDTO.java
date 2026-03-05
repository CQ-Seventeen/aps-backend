package com.santoni.iot.aps.application.schedule.dto;

import lombok.Data;

@Data
public class ScheduleLogDTO {

    private long logId;

    private int operateType;

    private String operateTime;

    private String detail;
}
