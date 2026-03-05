package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public record DeliveryTime(LocalDateTime value) implements Comparable<DeliveryTime> {

    public DeliveryTime {
        if (null == value) {
            throw new IllegalArgumentException("交付时间不可为空");
        }
    }

    @Override
    public int compareTo(@NotNull DeliveryTime o) {
        return value.compareTo(o.value);
    }

    public boolean isBefore(TimePeriod timePeriod) {
        if (null == timePeriod || null == timePeriod.getStart()) {
            return false;
        }
        return value.isBefore(timePeriod.getStart().value());
    }

    public boolean isAfter(TimePeriod timePeriod) {
        if (null == timePeriod || null == timePeriod.getEnd()) {
            return false;
        }
        return value.isAfter(timePeriod.getEnd().value());
    }
}
