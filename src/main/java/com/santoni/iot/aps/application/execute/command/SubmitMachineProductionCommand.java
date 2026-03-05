package com.santoni.iot.aps.application.execute.command;

import lombok.Data;

@Data
public class SubmitMachineProductionCommand {

    private long factoryId;

    private String date;
}
