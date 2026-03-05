package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CanWeaveMachinePlanQuery {

    private long weavingPartOrderId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
