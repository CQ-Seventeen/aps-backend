package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CancelTaskCommand {

    private long taskId;
}
