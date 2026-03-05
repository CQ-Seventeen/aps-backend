package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;

public record AssignTaskContext(WeavingPartOrder weavingPartOrder,
                                MachinePlan machinePlan,
                                StyleComponent styleComponent) {
}
