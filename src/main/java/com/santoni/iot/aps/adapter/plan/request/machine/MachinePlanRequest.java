package com.santoni.iot.aps.adapter.plan.request.machine;

import com.santoni.iot.aps.adapter.resource.request.PageQueryMachineRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachinePlanRequest extends PageQueryMachineRequest {

    private String startTime;

    private String endTime;
}
