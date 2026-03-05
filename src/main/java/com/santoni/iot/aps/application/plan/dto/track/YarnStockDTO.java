package com.santoni.iot.aps.application.plan.dto.track;

import lombok.Data;

@Data
public class YarnStockDTO {

    private String yarnId;

    private String yarnCode;

    private String lotNumber;

    private String supplierCode;

    private String twist;

    private String color;

    private double stockQuantity;

    private double transitQuantity;

    private String unit;
}
