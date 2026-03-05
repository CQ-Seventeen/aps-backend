package com.santoni.iot.aps.application.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTaskResultDTO {

    private List<AggregateMachineScheduleDTO> scheduleList = List.of();

    private String startTime;

    private String endTime;
}
