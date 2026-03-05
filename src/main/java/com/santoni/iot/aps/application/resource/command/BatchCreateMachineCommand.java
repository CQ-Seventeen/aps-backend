package com.santoni.iot.aps.application.resource.command;

import lombok.Data;

import java.util.List;

@Data
public class BatchCreateMachineCommand {

    private List<CreateMachineCommand> machineList;
}
