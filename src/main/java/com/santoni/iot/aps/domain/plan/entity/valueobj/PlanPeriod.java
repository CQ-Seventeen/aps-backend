package com.santoni.iot.aps.domain.plan.entity.valueobj;

import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class PlanPeriod {

    private final PlanStartTime start;

    private final PlanEndTime end;

    private PlanPeriod(PlanStartTime start, PlanEndTime end) {
        this.start = start;
        this.end = end;
    }

    public TimePeriod toTimePeriod() {
        return TimePeriod.of(start.value(), end.value());
    }

    public static PlanPeriod of(LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("计划起止时间必须指定");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("计划结束时间不得早于开始时间");
        }
        return new PlanPeriod(new PlanStartTime(startTime), new PlanEndTime(endTime));
    }

    public static PlanPeriod of(PlanStartTime startTime, PlanEndTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("计划起止时间必须指定");
        }
        if (endTime.value().isBefore(startTime.value())) {
            throw new IllegalArgumentException("计划结束时间不得早于开始时间");
        }
        return new PlanPeriod(startTime, endTime);
    }

    public static PlanPeriod byStartTime(PlanStartTime startTime, long seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("持续秒数必须大于0");
        }
        LocalDateTime end = startTime.value().plusSeconds(seconds);
        return new PlanPeriod(startTime, new PlanEndTime(end));
    }

    public long totalSeconds() {
        return Duration.between(start.value(), end.value()).getSeconds();
    }

    public BigDecimal percentOccupied(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(end.value()) || endTime.isBefore(start.value())) {
            return BigDecimal.ZERO;
        }
        if (start.value().isBefore(startTime) && end.value().isAfter(endTime)) {
            return BigDecimal.ONE;
        }
        var seconds = BigDecimal.valueOf(Duration.between(startTime, endTime).getSeconds());

        long occupySeconds;
        if (end.value().isBefore(endTime)) {
            occupySeconds = Duration.between(startTime, end.value()).getSeconds();
        } else {
            occupySeconds = Duration.between(start.value(), endTime).getSeconds();
        }
        return BigDecimal.valueOf(occupySeconds).divide(seconds, RoundingMode.HALF_UP);
    }

}
