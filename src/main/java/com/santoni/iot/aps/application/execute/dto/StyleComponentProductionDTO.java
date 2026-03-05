package com.santoni.iot.aps.application.execute.dto;

import lombok.Data;

@Data
public class StyleComponentProductionDTO {

    private String produceOrderCode;

    private String deliveryDate;

    private String styleCode;

    private String size;

    private String part;

    private int production;

    private int tdProduction;

    private int totalQuantity;

    private int leftQuantity;

    private double leftDays;
}
