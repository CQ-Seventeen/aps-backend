package com.santoni.iot.aps.adapter.plan.request.order;

import lombok.Data;

import java.util.List;

@Data
public class WeavingOrderPlanRequest {

    private long weavingOrderId;

    private String startTime;

    private String endTime;

    private List<String> machineTypeList;

    private List<String> bareSpandexTypeList;

    private Boolean highSpeed;
}
