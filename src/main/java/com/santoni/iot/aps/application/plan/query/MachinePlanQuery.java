package com.santoni.iot.aps.application.plan.query;

import com.santoni.iot.aps.application.resource.query.PageMachineQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachinePlanQuery extends PageMachineQuery {

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
