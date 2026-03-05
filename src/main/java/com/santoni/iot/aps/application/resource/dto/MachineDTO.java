package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;

import java.util.List;

@Data
public class MachineDTO {

    private long machineId;

    private String deviceId;

    private String machineCode;

    private Integer cylinderDiameter;

    private Integer needleSpacing;

    private Integer needleNumber;

    private String machineType;

    private String bareSpandexType;

    private boolean highSpeed;

    private List<MachineFeatureDTO> features;
}
