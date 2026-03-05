package com.santoni.iot.aps.application.plan.dto.order;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

import java.util.List;

@Data
public class WeavingPlannedMachineDTO {

    private long machineId;

    private String deviceId;

    private String machineCode;

    private List<TimePeriodDTO> availableTimePeriods;

    private List<TimePeriodDTO> unReachableTimePeriods;

    private List<WeavingPlannedPeriodDTO> occupiedTimePeriods;

    private List<WeavingPlannedPeriodDTO> otherOccupiedTimePeriods;

}
