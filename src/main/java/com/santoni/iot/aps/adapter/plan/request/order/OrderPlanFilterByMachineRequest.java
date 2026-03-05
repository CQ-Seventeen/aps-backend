package com.santoni.iot.aps.adapter.plan.request.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderPlanFilterByMachineRequest {

    private String produceOrderCode;

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;
}
