package com.santoni.iot.aps.domain.plan.entity.valueobj;

import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import lombok.Getter;

@Getter
public class PlannedQuantity {

    private final int value;

    private PlannedQuantity(int value) {
        this.value = value;
    }

    public static PlannedQuantity of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("计划数量必须不小于0");
        }
        return new PlannedQuantity(value);
    }

    public Quantity toQuantity() {
        return Quantity.of(this.value);
    }

    public boolean biggerThan(PlannedQuantity other) {
        return this.value > other.value;
    }

    public PlannedQuantity minus(PlannedQuantity minusQuantity) {
        return new PlannedQuantity(this.value - minusQuantity.value);
    }

    public PlannedQuantity minus(ProduceQuantity minusQuantity) {
        return new PlannedQuantity(this.value - minusQuantity.getValue());
    }

    public Quantity left(ProduceQuantity producedQuantity) {
        if (value <= producedQuantity.getValue()) {
            return Quantity.zero();
        }
        return Quantity.of( value - producedQuantity.getValue());
    }
}
