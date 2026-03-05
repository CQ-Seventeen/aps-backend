package com.santoni.iot.aps.domain.bom.entity;

import lombok.Getter;

import java.math.BigDecimal;

import static com.santoni.iot.aps.domain.plan.constant.PlanConstant.DEFAULT_PRODUCE_EFFICIENCY;

@Getter
public class ProduceEfficiency {

    private BigDecimal defaultEfficiency;

    private BigDecimal actualEfficiency;

    public ProduceEfficiency(BigDecimal defaultEfficiency, BigDecimal actualEfficiency) {
        this.defaultEfficiency = null == defaultEfficiency ? DEFAULT_PRODUCE_EFFICIENCY : defaultEfficiency;
        this.actualEfficiency = actualEfficiency;
    }

    public ProduceEfficiency(BigDecimal defaultEfficiency) {
        this.defaultEfficiency = null == defaultEfficiency ? DEFAULT_PRODUCE_EFFICIENCY : defaultEfficiency;
        this.actualEfficiency = BigDecimal.ZERO;
    }

    public BigDecimal getEfficiency() {
        if (null != actualEfficiency && actualEfficiency.compareTo(BigDecimal.ZERO) > 0) {
            return actualEfficiency;
        }
        return defaultEfficiency;
    }
}
