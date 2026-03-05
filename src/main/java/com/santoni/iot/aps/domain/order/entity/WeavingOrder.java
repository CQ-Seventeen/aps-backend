package com.santoni.iot.aps.domain.order.entity;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.ProgramFile;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class WeavingOrder {

    private WeavingOrderId id;

    private FactoryId factoryId;

    private WeavingOrderCode orderCode;

    private ProduceOrderId produceOrderId;

    private ProduceOrderCode produceOrderCode;

    private StyleDemand demand;

    private Quantity plannedQuantity;

    private DeliveryTime deliveryTime;

    private WeavingOrderStatus status;

    public int unPlannedQuantity() {
        return this.demand.getWeaveQuantity().getValue() - this.plannedQuantity.getValue();
    }

    public void changeDemandQuantity(StyleDemand newDemand) {
        if (!this.demand.getSkuCode().equals(newDemand.getSkuCode())) {
            throw new UnsupportedOperationException("织造单款式不允许修改");
        }
        this.demand = newDemand;
    }

    public void plannedBy(Quantity plannedQuantity) {
        if (null == this.plannedQuantity) {
            this.plannedQuantity = plannedQuantity;
        } else {
            this.plannedQuantity = this.plannedQuantity.plus(plannedQuantity);
        }
        this.status = this.status.toggleStatusAfterPlan(!this.plannedQuantity.lessThan(this.demand.getWeaveQuantity()));
    }

    public static WeavingOrder newOf(FactoryId factoryId,
                                     WeavingOrderCode code,
                                     ProduceOrderId produceOrderId,
                                     ProduceOrderCode produceOrderCode,
                                     StyleDemand demand,
                                     DeliveryTime deliveryTime) {
        return new WeavingOrder(null, factoryId, code, produceOrderId, produceOrderCode, demand,
                Quantity.zero(), deliveryTime, WeavingOrderStatus.CREATED);
    }

    public List<WeavingPartOrder> derivePartOrders(StyleSku sku, WeavingOrderId weavingOrderId,
                                                   ProgramFile program, TaskDetailId taskDetailId,
                                                   Figure figure, Unit unit, OrderComment comment) {
        List<StylePartDemand> demands = this.getDemand().divideToPartDemand(sku);
        List<WeavingPartOrder> res = Lists.newArrayListWithExpectedSize(demands.size());
        for (StylePartDemand demand : demands) {
            res.add(new WeavingPartOrder(null,
                    factoryId,
                    weavingOrderId,
                    this.produceOrderId,
                    this.produceOrderCode,
                    demand,
                    Collections.emptyList(),
                    Quantity.zero(),
                    this.deliveryTime,
                    program,
                    taskDetailId,
                    figure,
                    unit,
                    comment,
                    WeavingOrderStatus.CREATED
            ));
        }
        return res;
    }

    public WeavingOrder(WeavingOrderId id,
                        FactoryId factoryId,
                        WeavingOrderCode orderCode,
                        ProduceOrderId produceOrderId,
                        ProduceOrderCode produceOrderCode,
                        StyleDemand demand,
                        Quantity plannedQuantity,
                        DeliveryTime deliveryTime,
                        WeavingOrderStatus status) {
        this.id = id;
        this.factoryId = factoryId;
        this.orderCode = orderCode;
        this.produceOrderId = produceOrderId;
        this.produceOrderCode = produceOrderCode;
        this.demand = demand;
        this.plannedQuantity = plannedQuantity;
        this.deliveryTime = deliveryTime;
        this.status = status;
    }

    public boolean unPlanned() {
        return this.status == WeavingOrderStatus.CREATED
                || this.status == WeavingOrderStatus.PLANNING_PRODUCING
                || this.status == WeavingOrderStatus.PLANNING_NO_PRODUCE;
    }
}
