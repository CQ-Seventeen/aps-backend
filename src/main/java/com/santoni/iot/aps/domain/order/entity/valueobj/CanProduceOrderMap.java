package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;

import java.util.List;
import java.util.Map;

public record CanProduceOrderMap(Map<String, Map<String, StyleComponent>> componentMap,
                                 List<WeavingPartOrder> partOrders) {
}
