package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
public class StyleRequirement implements Comparable<StyleRequirement> {

    private long weavingPartOrderId;

    private StyleComponent styleComponent;

    private int quantity;

    private LocalDateTime deliveryTime;

    public String getKey() {
        return styleComponent.getSkuCode().value() + "-" + styleComponent.getPart().value();
    }

    public StyleRequirement(long weavingPartOrderId,
                            StyleComponent styleComponent,
                            int quantity,
                            LocalDateTime deliveryTime) {
        this.weavingPartOrderId = weavingPartOrderId;
        this.styleComponent = styleComponent;
        this.quantity = quantity;
        this.deliveryTime = deliveryTime;
    }

    public void arrangedQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public boolean hasLeft() {
        return this.quantity > 0;
    }

    @Override
    public int compareTo(@NotNull StyleRequirement other) {
        if (!this.deliveryTime.isEqual(other.deliveryTime)) {
            return this.deliveryTime.compareTo(other.deliveryTime);
        }
        return this.quantity - other.quantity;
    }
}
