package com.santoni.iot.aps.application.schedule.dto;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

@Data
public class MachineSchedulePeriodDTO {

    private long weavingPartOrderId;

    private String skuCode;

    private String part;

    private TimePeriodDTO timePeriod;

    private long plannedQuantity;
}
