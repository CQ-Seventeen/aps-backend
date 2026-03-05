package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;

import java.util.List;
import java.util.Map;

public record ProduceSkuDemand(List<SkuCode> skuCodeList, Map<String, List<StyleDemand>> demandMap) {
}
