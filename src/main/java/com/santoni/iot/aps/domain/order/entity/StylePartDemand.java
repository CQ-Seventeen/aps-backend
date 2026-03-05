package com.santoni.iot.aps.domain.order.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import lombok.Getter;

@Getter
public class StylePartDemand {

    private SkuCode skuCode;

    private StyleCode styleCode;

    private Symbol symbol;

    private Size size;

    private Part part;

    private Color color;

    private Quantity quantity;

    public StylePartDemand(SkuCode skuCode,
                           StyleCode styleCode,
                           Symbol symbol,
                           Size size,
                           Part part,
                           Color color,
                           Quantity quantity) {
        this.skuCode = skuCode;
        this.styleCode = styleCode;
        this.symbol = symbol;
        this.size = size;
        this.part = part;
        this.color = color;
        this.quantity = quantity;
    }

    public String getPartUniqueKey() {
        return null == part ? "" : part.value();
    }


    public String getUniqueKey() {
        return (null == skuCode ? "" : skuCode.value()) + "-" + (null == part ? "" : part.value());
    }
}
