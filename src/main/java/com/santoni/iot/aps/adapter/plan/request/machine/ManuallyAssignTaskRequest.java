package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

@Data
public class ManuallyAssignTaskRequest {

    private long weavingPartOrderId;

    private long machineId;

    private String planStartTime;

    private String planEndTime;

    private int quantity;
}
