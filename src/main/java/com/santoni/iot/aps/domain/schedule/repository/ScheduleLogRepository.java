package com.santoni.iot.aps.domain.schedule.repository;

import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleLog;
import com.santoni.iot.aps.domain.support.entity.PageData;

public interface ScheduleLogRepository {

    void logSchedule(ScheduleLog scheduleLog);

    PageData<ScheduleLog> pageQueryScheduleLog(SearchScheduleLog search);
}
