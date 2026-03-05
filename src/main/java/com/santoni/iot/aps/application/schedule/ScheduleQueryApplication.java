package com.santoni.iot.aps.application.schedule;

import com.santoni.iot.aps.application.schedule.dto.ScheduleLogDTO;
import com.santoni.iot.aps.application.schedule.query.PageScheduleLogQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;

public interface ScheduleQueryApplication {

    PageResult<ScheduleLogDTO> pageQueryScheduleLog(PageScheduleLogQuery query);
}
