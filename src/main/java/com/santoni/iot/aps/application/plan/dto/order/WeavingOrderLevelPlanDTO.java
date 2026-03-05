package com.santoni.iot.aps.application.plan.dto.order;

import com.santoni.iot.aps.application.resource.dto.MachineOptionsDTO;
import lombok.Data;

import java.util.List;

@Data
public class WeavingOrderLevelPlanDTO {

    private long weavingOrderId;

    private String weavingOrderCode;

    private String styleCode;

    private int totalQuantity;

    private String deliveryTime;

    private int plannedQuantity;

    private int unPlannedQuantity;

    private int produceQuantity;

    private String startTime;

    private String endTime;

    private List<WeavingPlannedMachineDTO> plannedMachines;

    private MachineOptionsDTO machineOptions;
}
