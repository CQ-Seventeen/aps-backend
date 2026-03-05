package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.StyleSpu;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.resource.entity.Machine;

import java.util.Map;

public record BuildMachineTaskDetailContext(Machine machine,
                                            StyleComponent styleComponent,
                                            StyleSku styleSku,
                                            StyleSpu styleSpu,
                                            WeavingPartOrder weavingPartOrder) {
}
