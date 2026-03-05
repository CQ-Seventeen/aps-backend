package com.santoni.iot.aps.adapter.schedule.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitScheduleTaskRequest {

    private List<Long> machineIds;

    private List<StyleWeaveDemandRequest> styleDemandList;

    private String startTime;

    private String endTime;
}
