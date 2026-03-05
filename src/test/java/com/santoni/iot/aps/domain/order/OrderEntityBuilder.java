package com.santoni.iot.aps.domain.order;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;

import java.time.LocalDateTime;

public class OrderEntityBuilder {

    public static WeavingOrder getWeavingOrder() {
        return WeavingOrder.newOf(
                new FactoryId(1),
                new WeavingOrderCode("weaving"),
                new ProduceOrderId(1),
                new ProduceOrderCode("produce"),
                StyleDemand.of(new SkuCode("style1XXL"),
                        new Color("红色"),
                        Quantity.of(1000)),
                new DeliveryTime(LocalDateTime.now().plusDays(30))
        );
    }

    public static WeavingOrder getWeavingOrderWithTask() {
        return new WeavingOrder(
                new WeavingOrderId(1),
                new FactoryId(1),
                new WeavingOrderCode("weaving"),
                new ProduceOrderId(1),
                new ProduceOrderCode("produce"),
                StyleDemand.of(new SkuCode("style1XXL"),
                        new Color("红色"),
                        Quantity.of(1000)),
                Quantity.zero(),
                new DeliveryTime(LocalDateTime.now().plusDays(30)),
                WeavingOrderStatus.CREATED
        );
    }

}
