package com.santoni.iot.aps.application.order.context;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;

import java.util.Map;

public record BuildProduceOrderContext(Map<String, StyleSku> styleSkuMap,
                                       Map<Long, Factory> factoryMap,
                                       Map<Long, FactoryTask> taskMap) {
}
