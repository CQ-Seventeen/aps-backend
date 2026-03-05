package com.santoni.iot.aps.application.execute.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EstimateMachineWeaveTimeQuery {

    private long weavingPartOrderId;

    private long machineId;

    private int quantity;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime canStartTime;

    private LocalDateTime canEndTime;
}
