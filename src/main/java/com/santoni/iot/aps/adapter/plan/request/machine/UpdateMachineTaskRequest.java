package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMachineTaskRequest {

    private long taskId;

    private String flag;
}
