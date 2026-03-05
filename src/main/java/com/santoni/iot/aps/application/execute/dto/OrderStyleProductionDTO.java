package com.santoni.iot.aps.application.execute.dto;

import lombok.Data;

@Data
public class OrderStyleProductionDTO {

    private String produceOrderCode;

    private String deliveryDate;

    private int startDays;

    private int leftDays;

    private String styleCode;

    private String size;

    private int tdProduction;

    private int totalQuantity;

    private int leftQuantity;

    private double leftMachineDays;
}
