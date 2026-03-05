package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleLogPO {

    private long id;

    private long instituteId;

    private long logId;

    private int type;

    private LocalDateTime operateTime;

    private long creatorId;

    private LocalDateTime createTime;

    private long deletedAt;
}
