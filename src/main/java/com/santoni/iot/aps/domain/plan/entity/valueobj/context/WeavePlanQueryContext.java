package com.santoni.iot.aps.domain.plan.entity.valueobj.context;

import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineOptions;

import java.time.LocalDateTime;
import java.util.List;

public record WeavePlanQueryContext(WeavingOrder weavingOrder,
                                    List<PlannedTask> taskList,
                                    MachineOptions machineOptions,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime) {
}
