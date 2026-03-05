package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskCommand {

    private long taskId;

    private String taskFlag;

}