package com.santoni.iot.aps.application.execute.context;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;

import java.util.List;
import java.util.Map;

public record BuildStyleProductionContext(List<ProduceOrder> produceOrderList,
                                          Map<Long, List<WeavingPartOrder>> partOrderMap,
                                          List<ProductionSum> productionList,
                                          Map<String, StyleSku> skuMap,
                                          ProduceDate date) {
}
