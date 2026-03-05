package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchScheduleTask extends PageParam {

    private final List<SubmitTaskStatus> status;

    public SearchScheduleTask(List<SubmitTaskStatus> status, CommonPageQuery query) {
        this.status = status;
        fromPageQuery(query);
    }
}
