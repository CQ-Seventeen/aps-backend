package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MachineCapacityQuery {

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
