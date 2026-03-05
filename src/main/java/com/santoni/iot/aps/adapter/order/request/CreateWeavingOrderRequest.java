package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

@Data
public class CreateWeavingOrderRequest {

    private String code;

    private long produceOrderId;

    private String skuCode;

    private int quantity;

    private String deliveryTime;

}
