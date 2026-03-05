package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.util.List;

@Data
public class OrderPlanFilterByMachineQuery {

    private long factoryId;

    private String produceOrderCode;

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;
}
