package com.santoni.iot.aps.application.schedule.query;

import com.santoni.iot.aps.application.plan.query.FilterMachineQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleTaskResultQuery {

    private long taskId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private FilterMachineQuery filterMachine;

}
