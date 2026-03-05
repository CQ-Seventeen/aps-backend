package com.santoni.iot.aps.application.plan.dto.machine;

import com.santoni.iot.aps.application.resource.dto.MachineDTO;
import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

import java.util.List;

@Data
public class MachinePlanDetailDTO {

    private MachineDTO machine;

    private double workloadSaturation;

    private List<TimePeriodDTO> availableTimePeriods;

    private List<TimePeriodDTO> unReachableTimePeriods;

    private List<MachinePlannedPeriodDTO> occupiedTimePeriods;
}
