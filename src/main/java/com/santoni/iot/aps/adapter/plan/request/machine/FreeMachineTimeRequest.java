package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class FreeMachineTimeRequest {

    private List<Long> machineIds;

    private String startTime;

    private String endTime;
}
