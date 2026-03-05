package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

@Data
public class AssignedTaskDetailReq {

    private long weavingPartOrderId;

    private String planStartTime;

    private String planEndTime;

    private int quantity;
}
