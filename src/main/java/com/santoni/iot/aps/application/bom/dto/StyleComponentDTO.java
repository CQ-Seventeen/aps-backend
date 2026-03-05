package com.santoni.iot.aps.application.bom.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StyleComponentDTO {

    private String skuCode;

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int number;

    private int ratio;

    private int type;

    private int cylinderDiameter;

    private int needleSpacing;

    private String programFile;

    private String description;

    private double expectedProduceTime;

    private double expectedWeight;

    private int standardNumber;

    private MachineRequirementDTO machineRequirement;

    private int dailyTheoreticalQuantity;

    private BigDecimal defaultProduceEfficiency;

    private BigDecimal actualProduceEfficiency;

    private List<YarnUsageDTO> yarnUsages;
}
