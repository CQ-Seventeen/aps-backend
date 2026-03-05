package com.santoni.iot.aps.domain.plan.entity.valueobj.context;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceResourceDemand;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryPlan;

import java.util.Map;

public record AssignOrderToFactoryContext(FactoryPlan factoryPlan,
                                          ProduceResourceDemand demand,
                                          Map<String, StyleSku> skuMap) {
}
