package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

@Data
public class OperateStyleComponentRequest {

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int cylinderDiameter;

    private int needleSpacing;

    private int type;

    private String programFileName;

    private String programFileUrl;

    private int number;

    private int ratio;

    private Double expectedProduceTime;

    private Double expectedWeight;

    private int standardNumber;

    private MachineRequireRequest requirement;

    private String description;
}
