package com.santoni.iot.aps.application.execute.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductionAggregateByMachineDTO {
    private int cylinderDiameter;
    private int needleSpacing;
    private int totalNum;
    private int runningNum;
    private int totalQuantity;
    private int produceQuantity;
    private int leftQuantity;
    private BigDecimal leftDaysBySingle;
}
