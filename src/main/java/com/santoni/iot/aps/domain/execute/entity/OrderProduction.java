package com.santoni.iot.aps.domain.execute.entity;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.execute.util.SumKeyUtil;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class OrderProduction {

    private ProduceOrder produceOrder;

    private List<WeaveProduceTrack> productionList;

    public static OrderProduction deriveFromOrder(ProduceOrder order, List<WeavingPartOrder> weavingPartOrders, Map<String, Map<String, StyleComponent>> componentMap) {
        List<WeaveProduceTrack> productionList = Lists.newArrayListWithExpectedSize(weavingPartOrders.size());
        for (var weavingPartOrder : weavingPartOrders) {
            var component = componentMap.getOrDefault(weavingPartOrder.getDemand().getSkuCode().value(), Collections.emptyMap())
                    .get(weavingPartOrder.getDemand().getPart().value());
            if (null == component) {
                continue;
            }
            productionList.add(WeaveProduceTrack.of(component, weavingPartOrder, weavingPartOrder.getDemand().getQuantity()));
        }
        return new OrderProduction(order, productionList);
    }

    public List<SumKey> getAllComponentLevelKey() {
        if (CollectionUtils.isEmpty(productionList)) {
            return List.of();
        }
        List<SumKey> res = Lists.newArrayListWithExpectedSize(productionList.size());
        for (var production : productionList) {
            res.add(new SumKey(SumKeyUtil.buildComponentLevelKey(produceOrder.getCode(), production.getComponent())));
        }
        return res;
    }

    public void collectProduction(List<ProductionSum> sumList) {
        if (CollectionUtils.isEmpty(sumList)) return;
        var sumMap = sumList.stream().collect(Collectors.toMap(it -> it.getKey().value(), it -> it, (k1, k2) -> k2));
        for (var production : productionList) {
            var key = SumKeyUtil.buildComponentLevelKey(produceOrder.getCode(), production.getComponent().getSkuCode(), production.getComponent().getPart());
            var sum = sumMap.get(key);
            if (null != sum) {
                production.setProduceQuantity(sum.getTillTodayQuantity());
            }
        }
    }

    public void collectMachineTask(List<PlannedTask> taskList) {
        if (CollectionUtils.isEmpty(taskList)) return;
        var taskMap = taskList.stream().collect(Collectors.groupingBy(it -> it.getSkuCode().value() + "," + it.getPart().value()));
        for (var production : productionList) {
            var tasks = taskMap.get(production.getComponent().getSkuCode().value() + "," + production.getComponent().getPart().value());
            if (CollectionUtils.isNotEmpty(tasks)) {
                production.collectPlannedTasks(tasks);
            }
        }
    }

    private OrderProduction(ProduceOrder produceOrder, List<WeaveProduceTrack> productionList) {
        this.produceOrder = produceOrder;
        this.productionList = productionList;
    }
}
