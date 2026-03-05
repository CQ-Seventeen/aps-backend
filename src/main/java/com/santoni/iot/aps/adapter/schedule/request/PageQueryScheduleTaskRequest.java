package com.santoni.iot.aps.adapter.schedule.request;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryScheduleTaskRequest extends CommonPageRequest {

    private List<Integer> statusList;

}
