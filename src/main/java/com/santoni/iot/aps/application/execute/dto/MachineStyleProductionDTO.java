package com.santoni.iot.aps.application.execute.dto;

import lombok.Data;

@Data
public class MachineStyleProductionDTO {

    private long machineId;

    private String deviceId;



    private String styleCode;

    private int quantity;

    private String startTime;

    private String endTime;
}
