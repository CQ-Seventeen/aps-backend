package com.santoni.iot.aps.adapter.plan.request.machine;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryMachineTaskRequest extends CommonPageRequest {

    private String produceOrderCode;

    private String styleCode;

    private String startTime;

    private String endTime;
}
