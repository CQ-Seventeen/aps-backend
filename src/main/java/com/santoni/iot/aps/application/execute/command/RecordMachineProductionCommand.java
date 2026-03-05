package com.santoni.iot.aps.application.execute.command;

import lombok.Data;

@Data
public class RecordMachineProductionCommand {

    private String orderCode;

    private String styleCode;

    private String size;

    private String part;

    private String deviceId;

    private String date;

    private int pieces;

    private int defectPieces;

    private String barCode;

    private int type;
}
