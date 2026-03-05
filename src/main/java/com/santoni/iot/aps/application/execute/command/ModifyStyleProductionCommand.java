package com.santoni.iot.aps.application.execute.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyStyleProductionCommand {

    private String styleCode;

    private String date;

    private int actualQuantity;
}
