package com.santoni.iot.aps.adapter.plan.request.factory;

import lombok.Data;

@Data
public class FactoryPlanByOrderRequest {

    private long produceOrderId;

    private long factoryId;

    private String startTime;

    private String endTime;

}
