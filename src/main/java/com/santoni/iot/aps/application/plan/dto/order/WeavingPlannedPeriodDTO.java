package com.santoni.iot.aps.application.plan.dto.order;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

@Data
public class WeavingPlannedPeriodDTO {

    private long segmentId;

    private TimePeriodDTO timePeriod;

    private String styleCode;

    private long plannedQuantity;

    private long produceQuantity;

    private int status;
}
