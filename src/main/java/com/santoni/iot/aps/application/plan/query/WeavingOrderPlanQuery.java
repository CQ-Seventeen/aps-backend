package com.santoni.iot.aps.application.plan.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WeavingOrderPlanQuery {

    private long weavingOrderId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private FilterMachineQuery filterMachine;
}
