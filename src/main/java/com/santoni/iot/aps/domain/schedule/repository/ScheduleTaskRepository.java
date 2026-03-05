package com.santoni.iot.aps.domain.schedule.repository;

import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleTask;
import com.santoni.iot.aps.domain.support.entity.PageData;

import java.util.List;

public interface ScheduleTaskRepository {

    void createScheduleTask(ScheduleTask task);

    void updateScheduleTask(ScheduleTask task);

    List<ScheduleTask> listUnprocessedTask();

    PageData<ScheduleTask> pageQueryScheduleTask(SearchScheduleTask search);

    ScheduleTask findScheduleTaskById(ScheduleTaskId taskId);

    List<ScheduleTask> listByTaskIds(List<ScheduleTaskId> taskIds);

}
