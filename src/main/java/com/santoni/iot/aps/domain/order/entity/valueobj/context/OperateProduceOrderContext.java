package com.santoni.iot.aps.domain.order.entity.valueobj.context;

import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.support.entity.Customer;

import java.util.Map;

public record OperateProduceOrderContext(Customer customer,
                                         Map<String, SkuCode> skuCodeMap) {
}
