package com.santoni.iot.aps.application.schedule.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DoScheduleCommand {
    private long taskId;
}
