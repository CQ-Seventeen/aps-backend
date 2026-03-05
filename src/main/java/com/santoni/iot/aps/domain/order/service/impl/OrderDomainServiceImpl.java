package com.santoni.iot.aps.domain.order.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.CanProduceOrderMap;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceResourceDemand;
import com.santoni.iot.aps.domain.order.service.OrderDomainService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public boolean canWeavingOrderModify(WeavingOrder old, WeavingOrder newOrder) {
        if (!old.getProduceOrderId().equals(newOrder.getProduceOrderId())) {
            return false;
        }
        if (!old.getOrderCode().equals(newOrder.getOrderCode())) {
            return false;
        }
        if (!old.getDemand().getSkuCode().equals(newOrder.getDemand().getSkuCode())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canProduceOrderModify(ProduceOrder old, ProduceOrder newOrder) {
        if (!old.getCode().equals(newOrder.getCode())) {
            return false;
        }
        if (!old.getCustomer().getCode().equals(newOrder.getCustomer().getCode())) {
            return false;
        }
        return true;
    }

    @Override
    public ProduceResourceDemand calculateProduceDemand(ProduceOrder produceOrder, Map<String, StyleSku> skuMap) {
        var demand = new ProduceResourceDemand(produceOrder);
        for (var styleDemand : produceOrder.getDemands()) {
            var sku = skuMap.get(styleDemand.getSkuCode().value());
            if (null == sku || CollectionUtils.isEmpty(sku.getComponents())) {
                continue;
            }
            for (var component : sku.getComponents()) {
                double seconds = component.totalSeconds(styleDemand.getOrderQuantity());
                demand.addSeconds(component.getMachineSize().getCylinderDiameter(), (long) seconds);
            }
        }
        return demand;
    }

    @Override
    public CanProduceOrderMap filterCanProduceOrder(List<Integer> cylinderDiameterList,
                                                    List<Integer> needleSpacingList,
                                                    Map<String, Map<String, StyleComponent>> componentMap,
                                                    List<WeavingPartOrder> partOrders) {
        if (CollectionUtils.isEmpty(cylinderDiameterList) && CollectionUtils.isEmpty(needleSpacingList)) {
            return new CanProduceOrderMap(componentMap, partOrders);
        }
        var partOrderMap = partOrders.stream().collect(Collectors.groupingBy(it -> it.getDemand().getSkuCode().value(),
                Collectors.toMap(it -> it.getDemand().getPartUniqueKey(), it -> it)));
        Map<String, Map<String, StyleComponent>> resComponentMap = Maps.newHashMap();
        List<WeavingPartOrder> resPartOrders = Lists.newArrayList();

        for (var sku : componentMap.entrySet()) {
            for (var component : sku.getValue().entrySet()) {
                if (CollectionUtils.isNotEmpty(cylinderDiameterList)
                        && !cylinderDiameterList.contains(component.getValue().getMachineSize().getCylinderDiameter().value())) {
                    continue;
                }
                if (CollectionUtils.isNotEmpty(needleSpacingList)
                        && !needleSpacingList.contains(component.getValue().getMachineSize().getNeedleSpacing().value())) {
                    continue;
                }
                var oldMap = resComponentMap.get(sku.getKey());
                if (MapUtils.isEmpty(oldMap)) {
                    Map<String, StyleComponent> newComponentMap = Maps.newHashMap();
                    newComponentMap.put(component.getKey(), component.getValue());
                    resComponentMap.put(sku.getKey(), newComponentMap);
                } else {
                    oldMap.put(component.getKey(), component.getValue());
                }

                var order = partOrderMap.getOrDefault(sku.getKey(), Collections.emptyMap()).get(component.getKey());
                if (null != order) {
                    resPartOrders.add(order);
                }
            }
        }
        return new CanProduceOrderMap(resComponentMap, resPartOrders);
    }
}
