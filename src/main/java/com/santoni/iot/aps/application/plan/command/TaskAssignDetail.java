package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskAssignDetail {

    private long weavingPartOrderId;

    private int quantity;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
