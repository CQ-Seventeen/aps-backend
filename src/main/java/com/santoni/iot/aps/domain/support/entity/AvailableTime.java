package com.santoni.iot.aps.domain.support.entity;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AvailableTime {

    private final List<TimePeriod> availablePeriod;

    private long totalSeconds;

    private final LocalDateTime from;

    private final LocalDateTime end;

    private double availablePercent = 1.0;

    public AvailableTime(List<TimePeriod> availablePeriod, LocalDateTime end) {
        this(availablePeriod, LocalDateTime.now(), end);
    }

    public AvailableTime(LocalDateTime from, LocalDateTime end) {
        this.from = from;
        this.end = end;
        this.availablePeriod = Lists.newArrayList(TimePeriod.of(from, end));
        this.totalSeconds = availablePeriod.get(0).getSeconds();
    }

    public AvailableTime(List<TimePeriod> availablePeriod, LocalDateTime startTime, LocalDateTime end) {
        this.availablePeriod = availablePeriod;
        this.end = end;
        this.from = startTime;
        if (CollectionUtils.isNotEmpty(availablePeriod)) {
            this.totalSeconds = availablePeriod.stream().mapToLong(TimePeriod::getSeconds).sum();
            long allSeconds = Duration.between(from, end).getSeconds();
            this.availablePercent = (double) totalSeconds / allSeconds;
        } else {
            this.totalSeconds = 0;
            this.availablePercent = 0.0;
        }
    }

    public boolean isAvailable() {
        return totalSeconds > 0;
    }
}
