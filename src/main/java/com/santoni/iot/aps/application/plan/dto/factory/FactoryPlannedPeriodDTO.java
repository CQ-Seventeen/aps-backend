package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

@Data
public class FactoryPlannedPeriodDTO {

    private String produceOrderCode;

    private String startTime;

    private String endTime;

    private double occupiedDays;
}
