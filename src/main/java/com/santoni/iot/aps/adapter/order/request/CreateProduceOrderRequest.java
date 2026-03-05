package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateProduceOrderRequest {

    private String code;

    private String customerCode;

    private List<StyleDemandRequest> styleDemands;

    private String deliveryTime;

}
