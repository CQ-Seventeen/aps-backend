package com.santoni.iot.aps.application.execute.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EstimateMaxWaveQuantityQuery {

    private long weavingPartOrderId;

    private long machineId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int needQuantity;
}
