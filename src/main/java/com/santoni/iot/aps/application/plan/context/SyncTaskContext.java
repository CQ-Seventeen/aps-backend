package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;

public record SyncTaskContext(WeavingPartOrder weavingPartOrder,
                              Machine machine,
                              StyleComponent styleComponent,
                              ProduceOrder produceOrder,
                              PlannedTask task) {
}
