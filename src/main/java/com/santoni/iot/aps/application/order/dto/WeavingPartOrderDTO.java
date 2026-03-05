package com.santoni.iot.aps.application.order.dto;

import lombok.Data;

@Data
public class WeavingPartOrderDTO {

    private long id;

    private long factoryId;

    private long weavingOrderId;

    private long produceOrderId;

    private String produceOrderCode;

    // StylePartDemand fields
    private String styleCode;

    private String symbolId;

    private String symbol;

    private String skuCode;

    private String sizeId;

    private String size;

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int quantity;

    // WeavingPartOrder fields
    private int plannedQuantity;

    private String deliveryDate;

    private String program;

    private String taskDetailId;

    private String figure;

    private String unit;

    private String comment;

    private int status;
}

