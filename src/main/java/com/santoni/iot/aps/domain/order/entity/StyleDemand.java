package com.santoni.iot.aps.domain.order.entity;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.DeliveryTime;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import lombok.Getter;

import java.util.List;

@Getter
public class StyleDemand {

    private SkuCode skuCode;

    private Symbol symbol;

    private StyleCode styleCode;

    private Size size;

    private Color color;

    private Quantity orderQuantity;

    private Quantity weaveQuantity;

    private Quantity sampleQuantity;

    private DeliveryTime deliveryTime;

    private StyleDemand(SkuCode skuCode,
                        Symbol symbol,
                        StyleCode styleCode,
                        Size size,
                        Color color,
                        Quantity orderQuantity,
                        Quantity weaveQuantity,
                        Quantity sampleQuantity,
                        DeliveryTime deliveryTime) {
        this.skuCode = skuCode;
        this.symbol = symbol;
        this.styleCode = styleCode;
        this.size = size;
        this.color = color;
        this.orderQuantity = orderQuantity;
        this.weaveQuantity = weaveQuantity;
        this.sampleQuantity = sampleQuantity;
        this.deliveryTime = deliveryTime;
    }

    public static StyleDemand of(SkuCode skuCode, Quantity quantity) {
        return of(skuCode, null, null, null, null,
                null, quantity, null, null);
    }

    public static StyleDemand of(SkuCode skuCode,
                                 Color color,
                                 Quantity orderQuantity,
                                 Quantity weaveQuantity,
                                 Quantity sampleQuantity) {
        return of(skuCode, null, null, null,
                color, orderQuantity, weaveQuantity, sampleQuantity, null);
    }

    public static StyleDemand of(SkuCode skuCode, Color color, Quantity orderQuantity, Quantity weaveQuantity) {
        return of(skuCode, null, null, null,
                color, orderQuantity, weaveQuantity, Quantity.zero(), null);
    }

    public static StyleDemand of(SkuCode skuCode, Color color, Quantity orderQuantity) {
        return of(skuCode, null, null, null,
                color, orderQuantity, orderQuantity, Quantity.zero(), null);
    }

    public static StyleDemand of(SkuCode skuCode,
                                 Symbol symbol,
                                 StyleCode styleCode,
                                 Size size,
                                 Color color,
                                 Quantity weaveQuantity,
                                 DeliveryTime deliveryTime) {
        return new StyleDemand(skuCode, symbol, styleCode, size, color, null,
                weaveQuantity, null, deliveryTime);
    }

    public static StyleDemand of(SkuCode skuCode,
                                 Symbol symbol,
                                 StyleCode styleCode,
                                 Size size,
                                 Color color,
                                 Quantity orderQuantity,
                                 Quantity weaveQuantity,
                                 Quantity sampleQuantity,
                                 DeliveryTime deliveryTime) {
        if (null == skuCode || null == weaveQuantity) {
            throw new IllegalArgumentException("需求款式和需求件数均不可为空");
        }
        return new StyleDemand(skuCode, symbol, styleCode, size, color, orderQuantity,
                weaveQuantity, sampleQuantity, deliveryTime);
    }

    public List<StylePartDemand> divideToPartDemand(StyleSku sku) {
        List<StylePartDemand> res = Lists.newArrayListWithExpectedSize(sku.getComponents().size());
        for (var component : sku.getComponents()) {
            int quantity = component.actualPieces(this.weaveQuantity);
            res.add(new StylePartDemand(
                    this.skuCode,
                    this.styleCode,
                    this.symbol,
                    this.size,
                    component.getPart(),
                    this.color,
                    Quantity.of(quantity)
            ));
        }
        return res;
    }
}
