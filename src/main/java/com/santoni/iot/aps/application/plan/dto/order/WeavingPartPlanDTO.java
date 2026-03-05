package com.santoni.iot.aps.application.plan.dto.order;

import lombok.Data;

@Data
public class WeavingPartPlanDTO {

    private long weavingOrderId;

    private long weavingPartOrderId;

    private String styleCode;

    private String size;

    private String skuCode;

    private String part;

    private String color;

    private int cylinderDiameter;

    private int needleSpacing;

    private int machineNumber;

    private int quantity;

    private int unPlannedQuantity;

    private int plannedQuantity;

    private String expectedFinishTime;

}
