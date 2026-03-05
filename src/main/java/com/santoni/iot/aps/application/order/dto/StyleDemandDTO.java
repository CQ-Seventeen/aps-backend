package com.santoni.iot.aps.application.order.dto;

import lombok.Data;

@Data
public class StyleDemandDTO {

    private String styleCode;

    private String size;

    private String sizeId;

    private String skuCode;

    private String symbolId;

    private String symbol;

    private String colorId;

    private String color;

    private int orderQuantity;

    private int weaveQuantity;

    private int sampleQuantity;

    private double expectedDays;
}
