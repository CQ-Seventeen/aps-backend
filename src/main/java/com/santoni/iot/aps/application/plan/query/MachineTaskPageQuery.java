package com.santoni.iot.aps.application.plan.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachineTaskPageQuery extends CommonPageQuery {

    private Long factoryId;

    private String produceOrderCode;

    private String styleCode;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private FilterMachineQuery filterMachine;
}
