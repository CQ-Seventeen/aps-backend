package com.santoni.iot.aps.domain.schedule.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Getter;

@Getter
public class StyleWeavePlan {

    private WeavingPartOrderId weavingPartOrderId;

    private ProduceOrderCode orderCode;

    private SkuCode skuCode;

    private Part part;

    private Quantity quantity;

    private TimePeriod timePeriod;

    public StyleWeavePlan(WeavingPartOrderId weavingPartOrderId,
                          ProduceOrderCode orderCode,
                          SkuCode skuCode,
                          Part part,
                          Quantity quantity,
                          TimePeriod timePeriod) {
        this.weavingPartOrderId = weavingPartOrderId;
        this.orderCode = orderCode;
        this.skuCode = skuCode;
        this.part = part;
        this.quantity = quantity;
        this.timePeriod = timePeriod;
    }
}
