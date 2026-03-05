package com.santoni.iot.aps.application.resource.command;

import lombok.Data;

import java.util.List;

@Data
public class CreateMachineCommand {
    private String deviceId;
    private String code;

    private long instituteId;
    private long factoryId;
    private long workshopId;
    private long machineGroupId;

    private Integer cylinderDiameter;
    private Integer needleSpacing;
    private Integer needleNumber;

    private String machineType;
    private String bareSpandexType;
    private boolean highSpeed;
    private List<MachineFeatureCommand> features;
}
