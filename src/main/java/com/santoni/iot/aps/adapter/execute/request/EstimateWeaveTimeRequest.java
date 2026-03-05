package com.santoni.iot.aps.adapter.execute.request;

import lombok.Data;

@Data
public class EstimateWeaveTimeRequest {

    private long machineId;

    private long weavingPartOrderId;

    private int quantity;

    private String startTime;

    private String endTime;

    private String canStartTime;

    private String canEndTime;

}
