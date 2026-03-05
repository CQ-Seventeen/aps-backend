package com.santoni.iot.aps.domain.plan.entity.valueobj.context;

import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;

import java.util.Map;

public record BatchAssignTaskContext(Map<Long, WeavingPartOrder> partOrderMap,
                                     Map<Long, MachinePlan> planMap) {
}
