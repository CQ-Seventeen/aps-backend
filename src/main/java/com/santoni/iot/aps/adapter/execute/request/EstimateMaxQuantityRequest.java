package com.santoni.iot.aps.adapter.execute.request;

import lombok.Data;

@Data
public class EstimateMaxQuantityRequest {

    private long weavingPartOrderId;

    private long machineId;

    private String startTime;

    private String endTime;

    private int needQuantity;

}
