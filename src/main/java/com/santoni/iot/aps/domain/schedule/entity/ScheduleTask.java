package com.santoni.iot.aps.domain.schedule.entity;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleTask {

    private ScheduleTaskId taskId;

    private InstituteId instituteId;

    private List<MachineId> machineIds;

    private List<StylePartWeaveDemand> styleDemandList;

    private TimePeriod timePeriod;

    private SubmitTaskStatus status;

    private ScheduleTaskResult result;

    private HandleTime handleTime;

    public static ScheduleTask newOf(List<MachineId> machineIds,
                                     List<StylePartWeaveDemand> styleDemandList,
                                     TimePeriod timePeriod) {
        return new ScheduleTask(null, null, machineIds, styleDemandList, timePeriod, SubmitTaskStatus.SUBMITTED, null, null);
    }

    public void beginToProcess() {
        if (null == handleTime) {
            this.handleTime = new HandleTime(null, LocalDateTime.now(), null);
        } else {
            this.handleTime.beginToProcess();
        }
        this.status = SubmitTaskStatus.PROCESSING;
    }

    public void finish(ScheduleTaskResult result) {
        this.handleTime.finish();
        this.result = result;
        this.status = SubmitTaskStatus.FINISHED;
    }

    public ScheduleTask(ScheduleTaskId taskId,
                        InstituteId instituteId,
                        List<MachineId> machineIds,
                        List<StylePartWeaveDemand> styleDemandList,
                        TimePeriod timePeriod,
                        SubmitTaskStatus status,
                        ScheduleTaskResult result,
                        HandleTime handleTime) {
        this.taskId = taskId;
        this.instituteId = instituteId;
        this.machineIds = machineIds;
        this.styleDemandList = styleDemandList;
        this.timePeriod = timePeriod;
        this.status = status;
        this.result = result;
        this.handleTime = handleTime;
    }
}
