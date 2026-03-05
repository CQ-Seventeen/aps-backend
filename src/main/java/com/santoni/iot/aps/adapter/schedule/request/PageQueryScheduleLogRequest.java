package com.santoni.iot.aps.adapter.schedule.request;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryScheduleLogRequest extends CommonPageRequest {

    private Integer type;

}
