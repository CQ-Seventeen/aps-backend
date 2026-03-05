package com.santoni.iot.aps.application.plan.dto.machine;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

@Data
public class MachinePlannedPeriodDTO {

    private long weavingPartOrderId;

    private String orderCode;

    private String part;

    private String size;

    private String styleCode;

    private long taskId;

    private long segmentId;

    private TimePeriodDTO timePeriod;

    private String estimateEndTime;

    private long plannedQuantity;

    private long produceQuantity;

    private int status;
}
