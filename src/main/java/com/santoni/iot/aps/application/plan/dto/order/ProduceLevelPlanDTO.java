package com.santoni.iot.aps.application.plan.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class ProduceLevelPlanDTO {

    private long produceOrderId;

    private String produceOrderCode;

    private int machineNumber;

    private int totalQuantity;

    private int plannedQuantity;

    private String deliveryTime;

    private String expectedFinishTime;

    private List<WeavingPartPlanDTO> weavingPlanList;

}
