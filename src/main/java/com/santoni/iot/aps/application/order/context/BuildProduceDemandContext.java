package com.santoni.iot.aps.application.order.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;

import java.util.List;
import java.util.Map;

public record BuildProduceDemandContext(Map<String, StyleSku> skuMap,
                                        Map<String, List<StyleDemand>> demandMap,
                                        Map<Integer, List<StyleComponent>> componentMap,
                                        List<Integer> cylinderList) {
}