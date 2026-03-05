package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManuallyAssignTaskCommand {

    private long weavingPartOrderId;

    private long machineId;

    private int plannedQuantity;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;
}
