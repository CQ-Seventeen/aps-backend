package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Getter;

@Getter
public class StylePartWeaveDemand {

    private WeavingPartOrderId weavingPartOrderId;

    private ProduceOrderCode orderCode;

    private SkuCode skuCode;

    private Part part;

    private TimePeriod timePeriod;

    private Quantity quantity;

    public StylePartWeaveDemand(WeavingPartOrderId weavingPartOrderId,
                                ProduceOrderCode orderCode,
                                SkuCode skuCode,
                                Part part,
                                TimePeriod timePeriod,
                                Quantity quantity) {
        this.weavingPartOrderId = weavingPartOrderId;
        this.orderCode = orderCode;
        this.skuCode = skuCode;
        this.part = part;
        this.timePeriod = timePeriod;
        this.quantity = quantity;
    }

    public int compareByEndTime(StylePartWeaveDemand other) {
        if (this.timePeriod.getEnd().value().isBefore(other.timePeriod.getEnd().value())) {
            return -1;
        }
        return 1;
    }

    public static StylePartWeaveDemand of(WeavingPartOrder order, TimePeriod timePeriod) {
        return new StylePartWeaveDemand(order.getId(), order.getProduceOrderCode(),
                order.getDemand().getSkuCode(), order.getDemand().getPart(), timePeriod, Quantity.of(order.unPlannedQuantity()));
    }
}
