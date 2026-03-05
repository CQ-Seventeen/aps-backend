package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleTaskResult {

    private List<ScheduleOnMachine> machinePlanList;

    private String suggestion;

    private String errorInfo;

    public ScheduleTaskResult(List<ScheduleOnMachine> machinePlanList, String suggestion, String errorInfo) {
        this.machinePlanList = machinePlanList;
        this.suggestion = suggestion;
        this.errorInfo = errorInfo;
    }
}
