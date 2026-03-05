package com.santoni.iot.aps.domain.execute.entity.valueobj;

import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ProduceQuantity {

    private final int value;

    private ProduceQuantity(int value) {
        this.value = value;
    }

    public static ProduceQuantity of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("生产数量必须不小于0");
        }
        return new ProduceQuantity(value);
    }

    public static ProduceQuantity zero() {
        return new ProduceQuantity(0);
    }

    public ProduceQuantity minus(ProduceQuantity operand) {
        return new ProduceQuantity(this.value - operand.value);
    }

    public ProduceQuantity minus(Quantity operand) {
        return new ProduceQuantity(this.value - operand.getValue());
    }

    public ProduceQuantity plus(ProduceQuantity operand) {
        return new ProduceQuantity(this.value + operand.value);
    }

    public ProduceQuantity plus(Quantity operand) {
        return new ProduceQuantity(this.value + operand.getValue());
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean biggerThan(ProduceQuantity operand) {
        return value > operand.value;
    }

    public boolean achieve(PlannedQuantity plannedQuantity) {
        return value >= plannedQuantity.getValue();
    }

    public ProduceQuantity applyAlter(AlteredQuantity alteredQuantity) {
        return new ProduceQuantity(this.value + alteredQuantity.getValue());
    }

    public ProduceQuantity copy() {
        return new ProduceQuantity(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProduceQuantity that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
