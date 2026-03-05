package com.santoni.iot.aps.adapter.plan.request.factory;

import lombok.Data;

@Data
public class FactoryProducingOrderRequest {

    private long factoryId;

    private String date;
}
