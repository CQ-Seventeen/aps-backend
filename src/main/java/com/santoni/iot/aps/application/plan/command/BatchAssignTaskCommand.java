package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BatchAssignTaskCommand {

    private List<MachineAssignTaskCommand> machineAssignList;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
