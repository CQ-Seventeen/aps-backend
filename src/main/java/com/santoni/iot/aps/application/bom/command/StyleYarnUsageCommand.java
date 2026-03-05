package com.santoni.iot.aps.application.bom.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StyleYarnUsageCommand {

    private String yarnId;

    private String yarnCode;

    private String lotNumber;

    private String supplierCode;

    private String twist;

    private String color;

    private BigDecimal percentage;
}
