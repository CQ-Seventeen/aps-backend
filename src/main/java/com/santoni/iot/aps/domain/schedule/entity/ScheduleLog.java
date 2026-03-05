package com.santoni.iot.aps.domain.schedule.entity;

import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.schedule.entity.constant.ScheduleOperateType;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.LogId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.OperateTime;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleLog {

    private LogId logId;

    private ScheduleOperateType type;

    private PlannedTask plannedTask;

    private ScheduleTask scheduleTask;

    private OperateTime operateTime;

    public ScheduleLog(LogId logId, ScheduleOperateType type, PlannedTask plannedTask, ScheduleTask scheduleTask, OperateTime operateTime) {
        this.logId = logId;
        this.type = type;
        this.plannedTask = plannedTask;
        this.scheduleTask = scheduleTask;
        this.operateTime = operateTime;
    }

    public static ScheduleLog fromPlannedTask(TaskId taskId, PlannedTask plannedTask) {
        return new ScheduleLog(new LogId(taskId.value()), ScheduleOperateType.STYLE_MACHINE, plannedTask, null, new OperateTime(LocalDateTime.now()));
    }

    public static ScheduleLog fromScheduleTask(ScheduleTask scheduleTask) {
        return new ScheduleLog(new LogId(scheduleTask.getTaskId().value()), ScheduleOperateType.MULTI_STYLE_MACHINE, null, scheduleTask, new OperateTime(LocalDateTime.now()));
    }

}
