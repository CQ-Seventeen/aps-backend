package com.santoni.iot.aps.application.schedule.assembler;

import com.santoni.iot.aps.application.schedule.query.PageScheduleTaskQuery;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SubmitTaskStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ScheduleAssembler {

    public SearchScheduleTask composeSearchScheduleTask(PageScheduleTaskQuery query) {
        List<SubmitTaskStatus> statusList = CollectionUtils.isEmpty(query.getStatus()) ? List.of()
                : query.getStatus().stream().map(SubmitTaskStatus::findByCode).toList();
        return new SearchScheduleTask(statusList, query);
    }
}
