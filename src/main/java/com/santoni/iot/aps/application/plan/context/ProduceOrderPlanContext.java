package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;

import java.util.List;
import java.util.Map;

public record ProduceOrderPlanContext(Map<Long, List<WeavingPartOrder>> partOrderMap,
                                      Map<String, Map<String, Map<String, StyleComponent>>> componentMap) {
}
