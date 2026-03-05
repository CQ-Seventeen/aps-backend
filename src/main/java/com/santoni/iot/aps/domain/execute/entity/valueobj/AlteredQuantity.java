package com.santoni.iot.aps.domain.execute.entity.valueobj;

import lombok.Getter;

@Getter
public class AlteredQuantity {

    private final int value;

    public AlteredQuantity(int value) {
        this.value = value;
    }

    public static AlteredQuantity from(ProduceQuantity oldValue, ProduceQuantity newValue) {
        return new AlteredQuantity(newValue.getValue() - oldValue.getValue());
    }

    public static AlteredQuantity from(ProduceQuantity quantity, boolean add) {
        return new AlteredQuantity(add ? quantity.getValue() : -quantity.getValue());
    }
}
