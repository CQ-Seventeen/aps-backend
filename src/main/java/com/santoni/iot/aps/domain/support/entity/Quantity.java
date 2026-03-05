package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import lombok.Getter;

@Getter
public class Quantity {

    private int value;

    private Quantity(int value) {
        this.value = value;
    }

    public static Quantity of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("数量必须不小于0");
        }
        return new Quantity(value);
    }

    public static Quantity zero() {
        return new Quantity(0);
    }

    public Quantity minusProduceQuantity(ProduceQuantity operand) {
        return new Quantity(this.value - operand.getValue());
    }

    public Quantity minus(Quantity operand) {
        return new Quantity(this.value - operand.getValue());
    }

    public Quantity plus(Quantity operand) {
        return new Quantity(this.value + operand.getValue());
    }

    public boolean lessThan(Quantity other) {
        return this.value < other.value;
    }

    public boolean equal(Quantity other) {
        return this.value == other.value;
    }

    public void plusOne() {
        this.value = value + 1;
    }

    public boolean isZero() {
        return this.value == 0;
    }
}
