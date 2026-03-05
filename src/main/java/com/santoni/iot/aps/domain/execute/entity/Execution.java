package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.execute.entity.valueobj.ProducePeriod;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import lombok.Getter;

@Getter
public class Execution {

    private ProducePeriod period;

    private ProduceQuantity quantity;

    public Execution(ProducePeriod period, ProduceQuantity quantity) {
        this.period = period;
        this.quantity = quantity;
    }

    public static Execution of(ProduceQuantity quantity, ProducePeriod period) {
        if (null == period) {
            throw new IllegalArgumentException("任务执行需指定时间段");
        }
        return new Execution(period, quantity);
    }

    public static Execution of(ProducePeriod period) {
        if (null == period) {
            throw new IllegalArgumentException("任务执行需指定时间段");
        }
        return new Execution(period, ProduceQuantity.zero());
    }
}
