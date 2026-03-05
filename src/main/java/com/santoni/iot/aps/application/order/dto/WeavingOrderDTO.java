package com.santoni.iot.aps.application.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeavingOrderDTO {

    private long orderId;

    private String orderCode;

    private long produceOrderId;

    private String produceOrderCode;

    private String styleCode;

    private String sizeId;

    private String size;

    private String skuCode;

    private String symbolId;

    private String symbol;

    private String colorId;

    private String color;

    private int quantity;

    private int plannedQuantity;

    private int unPlannedQuantity;

    private String deliveryTime;

    private String executeStartTime;

    private String executeEndTime;

    private int produceQuantity;

    private int status;
}
