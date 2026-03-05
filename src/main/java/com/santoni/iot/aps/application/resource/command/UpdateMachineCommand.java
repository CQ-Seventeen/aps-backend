package com.santoni.iot.aps.application.resource.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMachineCommand extends CreateMachineCommand {
    private long machineId;
}
