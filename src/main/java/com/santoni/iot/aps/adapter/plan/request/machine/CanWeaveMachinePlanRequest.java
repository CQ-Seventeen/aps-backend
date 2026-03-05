package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

@Data
public class CanWeaveMachinePlanRequest {

    private long weavingPartOrderId;

    private String startTime;

    private String endTime;
}
