package com.santoni.iot.aps.domain.execute.util;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;

public class SumKeyUtil {

    public static String buildComponentLevelKey(ProduceOrderCode produceOrder, SkuCode skuCode, Part part) {
        return produceOrder.value() + "," + skuCode.value() + "," + part.value();
    }

    public static String buildComponentLevelKey(ProduceOrderCode produceOrder, StyleComponent styleComponent) {
        return buildComponentLevelKey(produceOrder, styleComponent.getSkuCode(), styleComponent.getPart());
    }

    public static String buildSkuLevelKey(ProduceOrderCode produceOrder, SkuCode skuCode) {
        return produceOrder.value() + "," + skuCode.value();
    }
}
