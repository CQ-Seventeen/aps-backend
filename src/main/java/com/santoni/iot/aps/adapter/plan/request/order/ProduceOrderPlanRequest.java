package com.santoni.iot.aps.adapter.plan.request.order;

import lombok.Data;

@Data
public class ProduceOrderPlanRequest {

    private Integer planStatus;

    private String deliveryStartTime;

    private String deliveryEndTime;

    private String search;

}
