package com.santoni.iot.aps.application.order.command;

import lombok.Data;

@Data
public class StyleDemandCommand {

    private String skuCode;

    private String styleCode;

    private String sizeId;

    private String size;

    private String symbolId;

    private String symbol;

    private String colorId;

    private String color;

    private int orderQuantity;

    private Integer weaveQuantity;

    private Integer sampleQuantity;
}
