package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FactoryPlanQuery {

    private long produceOrderId;

    private long factoryId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
