package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

@Data
public class StyleDemandRequest {

    private String skuCode;

    private String color;

    private int orderQuantity;

    private Integer weaveQuantity;

    private Integer sampleQuantity;
}
