package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScheduleTaskPO extends BasePO {

    private Long id;

    private long instituteId;

    private String machineIds;

    private String styleDemands;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int status;

    private String scheduleResult;

    private LocalDateTime beginProcessTime;

    private LocalDateTime finishTime;
}
