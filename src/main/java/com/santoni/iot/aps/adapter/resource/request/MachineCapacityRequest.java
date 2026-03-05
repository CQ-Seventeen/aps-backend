package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;

@Data
public class MachineCapacityRequest {

    private String startTime;

    private String endTime;
}
