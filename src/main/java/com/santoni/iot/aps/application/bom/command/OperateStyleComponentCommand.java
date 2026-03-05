package com.santoni.iot.aps.application.bom.command;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OperateStyleComponentCommand {

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

    private MachineRequirementCommand requirement;

    private String description;

    private BigDecimal defaultProduceEfficiency;

    private BigDecimal actualProduceEfficiency;

    private List<StyleYarnUsageCommand> yarnUsages;

    private Double finishWidth;

    private Double finishLength;

    private Double wasteRate;

    private String dye;
}
