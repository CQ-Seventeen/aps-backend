package com.santoni.iot.aps.application.execute.dto;

import lombok.Data;

@Data
public class MachineDailyProductionDTO {

    private String produceOrderCode;

    private String styleCode;

    private String size;

    private String part;

    private String deviceId;

    private int pieces;

    private String date;
}
