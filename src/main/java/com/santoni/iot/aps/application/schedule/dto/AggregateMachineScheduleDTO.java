package com.santoni.iot.aps.application.schedule.dto;

import lombok.Data;

import java.util.List;

@Data
public class AggregateMachineScheduleDTO {

    private int cylinderDiameter;

    private List<MachineScheduleResultDTO> machineScheduleList;
}
