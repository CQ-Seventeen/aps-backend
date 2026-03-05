package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;

import java.util.List;
import java.util.Map;

public record BuildFactoryPlanContext(Map<String, StyleSku> skuMap,
                                      Map<String, StyleDemand> demandMap,
                                      Map<Integer, List<StyleComponent>> componentMap,
                                      List<Integer> cylinderList,
                                      List<FactoryTask> taskList,
                                      Map<Integer, List<Machine>> machineMap) {
}