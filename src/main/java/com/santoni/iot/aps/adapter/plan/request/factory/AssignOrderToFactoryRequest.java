package com.santoni.iot.aps.adapter.plan.request.factory;

import lombok.Data;

@Data
public class AssignOrderToFactoryRequest {

    private long produceOrderId;

    private long factoryId;
}
