package com.santoni.iot.aps.domain.order.service;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.CanProduceOrderMap;
import com.santoni.iot.aps.domain.order.entity.valueobj.DeliveryTime;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceResourceDemand;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface OrderDomainService {

    boolean canWeavingOrderModify(WeavingOrder old, WeavingOrder newOrder);

    boolean canProduceOrderModify(ProduceOrder old, ProduceOrder newOrder);

    ProduceResourceDemand calculateProduceDemand(ProduceOrder produceOrder, Map<String, StyleSku> skuMap);

    CanProduceOrderMap filterCanProduceOrder(List<Integer> cylinderDiameterList,
                                             List<Integer> needleSpacingList,
                                             Map<String, Map<String, StyleComponent>> componentMap,
                                             List<WeavingPartOrder> partOrders);
}
