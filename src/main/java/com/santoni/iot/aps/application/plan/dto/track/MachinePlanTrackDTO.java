package com.santoni.iot.aps.application.plan.dto.track;

import lombok.Data;

@Data
public class MachinePlanTrackDTO {

    private long produceOrderId;

    private String produceOrderCode;

    private String styleCode;

    private String size;

    private String component;

    private String machineDeviceId;

    private int plannedQuantity;

    private int producedQuantity;
}
