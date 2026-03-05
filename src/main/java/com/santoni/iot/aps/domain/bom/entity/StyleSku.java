package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.bom.entity.valueobj.ExpectedProduceTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineDays;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class StyleSku {

    private StyleCode styleCode;

    private SkuCode code;

    private Size size;

    private ProduceOrderCode produceOrderCode;

    private List<StyleComponent> components;

    private ExpectedProduceTime expectedProduceTime;

    private StyleSku(StyleCode styleCode, SkuCode code, Size size) {
        this.styleCode = styleCode;
        this.code = code;
        this.size = size;
    }

    public StyleSku(StyleCode styleCode,
                    SkuCode code,
                    Size size,
                    ProduceOrderCode produceOrderCode,
                    List<StyleComponent> components,
                    ExpectedProduceTime expectedProduceTime) {
        this.styleCode = styleCode;
        this.code = code;
        this.size = size;
        this.produceOrderCode = produceOrderCode;
        this.components = components;
        this.expectedProduceTime = expectedProduceTime;
    }

    public static StyleSku of(StyleCode styleCode, ProduceOrderCode orderCode, Size size) {
        var skuCode = styleCode.value() + size.value();
        return new StyleSku(styleCode, new SkuCode(skuCode), size, orderCode, null, null);
    }

    public static StyleSku of(StyleCode styleCode, Size size) {
        var skuCode = styleCode.value() + size.value();
        return new StyleSku(styleCode, new SkuCode(skuCode), size);
    }

    public void addComponents(List<StyleComponent> components) {
        this.components = components;
    }

    public boolean hasPart(String part) {
        return CollectionUtils.isNotEmpty(components) && components.stream().anyMatch(it -> StringUtils.equals(it.getPart().value(), part));
    }

    public void calculateExpectedProduceTime() {
        if (CollectionUtils.isEmpty(components)) {
            this.expectedProduceTime = new ExpectedProduceTime(0.0);
            return;
        }
        double res = 0;
        for (var component : components) {
            res += component.onePieceSeconds();
        }
        this.expectedProduceTime = new ExpectedProduceTime(res);
    }

    public MachineDays calculateMachineDays(Quantity quantity) {
        if (null == this.expectedProduceTime) {
            calculateExpectedProduceTime();
        }
        return MachineDays.fromSeconds((long) Math.ceil(this.expectedProduceTime.value() * quantity.getValue()));
    }

    public Map<String, StyleComponent> getComponentMap() {
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyMap();
        }
        return components.stream().collect(Collectors.toMap(StyleComponent::getUniqueKey, it -> it, (v1, v2) -> v1));
    }
}
