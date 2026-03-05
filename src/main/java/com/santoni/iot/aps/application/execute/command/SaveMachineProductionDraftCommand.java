package com.santoni.iot.aps.application.execute.command;

import lombok.Data;

import java.util.List;

@Data
public class SaveMachineProductionDraftCommand {

    private long factoryId;

    private String date;

    private List<RecordMachineProductionCommand> records;
}
