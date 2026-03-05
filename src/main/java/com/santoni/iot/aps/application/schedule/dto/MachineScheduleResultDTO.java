package com.santoni.iot.aps.application.schedule.dto;

import com.santoni.iot.aps.application.resource.dto.MachineDTO;
import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

import java.util.List;

@Data
public class MachineScheduleResultDTO {

    private MachineDTO machine;

    private List<TimePeriodDTO> availableTimePeriods;

    private List<TimePeriodDTO> unReachableTimePeriods;

    private List<TimePeriodDTO> occupiedTimePeriods;

    private List<MachineSchedulePeriodDTO> scheduleTimePeriods;
}
