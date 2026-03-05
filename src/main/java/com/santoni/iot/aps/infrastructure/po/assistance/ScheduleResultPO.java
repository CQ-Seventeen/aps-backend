package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleResultPO {

    private List<ScheduleOnMachinePO> machinePlanList;

    private String suggestion;

    private String errorInfo;
}
