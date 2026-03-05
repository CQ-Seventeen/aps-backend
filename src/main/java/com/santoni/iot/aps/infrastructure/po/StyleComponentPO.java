package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StyleComponentPO {

    private Long id;

    private long instituteId;

    private String produceOrderCode;

    private String skuCode;

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int type;

    private String programFile;

    private String programFileUrl;

    private int number;

    private int ratio;

    private int cylinderDiameter;

    private int needleSpacing;

    private String description;

    private Double expectedProduceTime;

    private Double expectedWeight;

    private int standardNumber;

    private String machineRequirement;

    private BigDecimal defaultEfficiency;

    private BigDecimal actualEfficiency;

    private String yarnUsage;

    private double finishWidth;

    private double finishLength;

    private double wasteRate;

    private String dye;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
