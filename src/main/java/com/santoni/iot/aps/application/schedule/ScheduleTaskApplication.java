package com.santoni.iot.aps.application.schedule;

import com.santoni.iot.aps.application.schedule.command.DoScheduleCommand;
import com.santoni.iot.aps.application.schedule.command.SubmitScheduleCommand;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskDTO;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskResultDTO;
import com.santoni.iot.aps.application.schedule.query.PageScheduleTaskQuery;
import com.santoni.iot.aps.application.schedule.query.ScheduleTaskResultQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;

public interface ScheduleTaskApplication {

    void submitScheduleTask(SubmitScheduleCommand command);

    void doScheduleSolve(DoScheduleCommand command);

    ScheduleTaskResultDTO directRecommend(SubmitScheduleCommand command);

    PageResult<ScheduleTaskDTO> pageQueryScheduleTask(PageScheduleTaskQuery query);

    ScheduleTaskResultDTO getScheduleTaskResult(ScheduleTaskResultQuery query);
}
