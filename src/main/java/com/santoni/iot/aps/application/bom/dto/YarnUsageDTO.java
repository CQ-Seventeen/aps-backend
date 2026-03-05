package com.santoni.iot.aps.application.bom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YarnUsageDTO {

    private String yarnId;

    private String yarnCode;

    private String lotNumber;

    private String supplierCode;

    private String twist;

    private String color;

    private BigDecimal percentage;

    private BigDecimal weight;
}
