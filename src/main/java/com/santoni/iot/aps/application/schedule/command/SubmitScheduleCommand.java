package com.santoni.iot.aps.application.schedule.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmitScheduleCommand {

    private List<Long> machineIds;

    private List<StyleWeaveDemandCommand> styleDemandList;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
