package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanStartTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class TimePeriod {

    private final StartTime start;

    private final EndTime end;

    private final long seconds;

    private TimePeriod(StartTime start, EndTime end) {
        this.start = start;
        this.end = end;
        this.seconds = Duration.between(start.value(), end.value()).toSeconds();
    }


    public static TimePeriod of(LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不得早于开始时间");
        }
        return new TimePeriod(new StartTime(startTime), new EndTime(endTime));
    }

    public static TimePeriod of(StartTime startTime, EndTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        if (endTime.value().isBefore(startTime.value())) {
            throw new IllegalArgumentException("结束时间不得早于开始时间");
        }
        return new TimePeriod(startTime, endTime);
    }

    public static TimePeriod fromPlan(PlanStartTime startTime, PlanEndTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        return of(startTime.value(), endTime.value());
    }

    public static TimePeriod byStartTime(StartTime startTime, long seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("持续秒数必须大于0");
        }
        LocalDateTime end = startTime.value().plusSeconds(seconds);
        return new TimePeriod(startTime, new EndTime(end));
    }

    public Pair<UnReachableTimePeriod, TimePeriod> splitTimePeriodByNow(LocalDateTime now) {
        if (start.value().isAfter(now)) {
            return Pair.of(null, this);
        }
        if (end.value().isBefore(now)) {
            return Pair.of(toUnReachableTimePeriod(), null);
        }
        return Pair.of(UnReachableTimePeriod.of(start.value(), now), of(now, end.value()));
    }

    private UnReachableTimePeriod toUnReachableTimePeriod() {
        return UnReachableTimePeriod.of(start, end);
    }

}
