package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class CanScheduleMachinePlanRequest {

    private List<Long> weavingPartOrderIds;
}
