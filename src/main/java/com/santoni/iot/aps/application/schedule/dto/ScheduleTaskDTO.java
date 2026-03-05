package com.santoni.iot.aps.application.schedule.dto;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleTaskDTO {

    private long taskId;

    private List<Long> machineIds;

    private List<StyleWeaveDemandDTO> styleDemands;

    private TimePeriodDTO timePeriod;

    private int status;

    private String suggestion;

    private String errorInfo;

    private String submitTime;

    private String beginToProcessTime;

    private String finishTime;

}
