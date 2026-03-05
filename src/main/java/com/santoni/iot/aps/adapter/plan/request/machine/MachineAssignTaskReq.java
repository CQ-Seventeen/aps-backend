package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class MachineAssignTaskReq {

    private long machineId;

    private List<AssignedTaskDetailReq> taskList;
}
