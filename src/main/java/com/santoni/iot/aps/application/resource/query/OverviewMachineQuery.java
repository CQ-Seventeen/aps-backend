package com.santoni.iot.aps.application.resource.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OverviewMachineQuery {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
