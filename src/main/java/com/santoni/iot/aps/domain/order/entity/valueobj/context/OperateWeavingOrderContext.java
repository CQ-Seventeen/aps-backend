package com.santoni.iot.aps.domain.order.entity.valueobj.context;

import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;

public record OperateWeavingOrderContext(ProduceOrder produceOrder,
                                         SkuCode skuCode) {
}
