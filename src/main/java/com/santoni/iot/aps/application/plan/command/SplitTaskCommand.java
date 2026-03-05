package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SplitTaskCommand {

    private long taskId;

    private long taskSegmentId;

    private long remainQuantity;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;
}
