package com.santoni.iot.aps.domain.bom.entity.valueobj;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;

import java.util.List;
import java.util.Map;

public record SkuAndComponentMap(Map<String, StyleSku> skuMap,
                                 Map<Integer, List<StyleComponent>> cylinderComponentMap) {
}
