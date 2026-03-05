package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class BatchAssignTaskRequest {

    private List<MachineAssignTaskReq> machineTaskList;

    private String startTime;

    private String endTime;
}
