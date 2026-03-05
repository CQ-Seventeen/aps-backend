package com.santoni.iot.aps.application.execute.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectStyleProductionCommand {

    private String styleCode;

    private LocalDateTime time;

    private int pieces;
}
