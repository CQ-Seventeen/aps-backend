package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.schedule.entity.constant.ScheduleOperateType;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchScheduleLog extends PageParam {

    private final ScheduleOperateType type;

    public SearchScheduleLog(ScheduleOperateType type, CommonPageQuery query) {
        this.type = type;
        fromPageQuery(query);
    }
}
