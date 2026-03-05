package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleOnMachinePO {

    private List<StyleWeaveSchedulePO> styleWeaveScheduleList;

    private long machineId;
}
