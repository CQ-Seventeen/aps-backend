package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateMachineRequest {

    private String deviceId;

    private String code;

    private long factoryId;

    private long workshopId;

    private long machineGroupId;

    private Integer cylinderDiameter;

    private Integer needleSpacing;

    private Integer needleNumber;

    private String machineType;

    private String bareSpandexType;

    private boolean highSpeed;

    private List<MachineFeatureRequest> featureList;

}
