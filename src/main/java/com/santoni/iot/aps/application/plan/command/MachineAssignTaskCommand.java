package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.util.List;

@Data
public class MachineAssignTaskCommand {

    private long machineId;

    private List<TaskAssignDetail> assignList;

}
