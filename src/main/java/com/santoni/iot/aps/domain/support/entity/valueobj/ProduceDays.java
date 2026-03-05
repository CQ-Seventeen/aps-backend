package com.santoni.iot.aps.domain.support.entity.valueobj;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProduceDays {

    private BigDecimal value;

    private ProduceDays(BigDecimal value) {
        this.value = value;
    }

    public static ProduceDays of(BigDecimal value) {
        if (BigDecimal.ZERO.compareTo(value) < 0) {
            throw new IllegalArgumentException("机器天数必须不小于0");
        }
        return new ProduceDays(value);
    }

    public static ProduceDays zero() {
        return new ProduceDays(BigDecimal.ZERO);
    }

    public void add(BigDecimal days) {
        this.value = value.add(days);
    }


}
