package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanStartTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class UnReachableTimePeriod {

    private final StartTime start;

    private final EndTime end;

    private final long seconds;

    private UnReachableTimePeriod(StartTime start, EndTime end) {
        this.start = start;
        this.end = end;
        this.seconds = Duration.between(start.value(), end.value()).toSeconds();
    }

    public static UnReachableTimePeriod of(LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不得早于开始时间");
        }
        return new UnReachableTimePeriod(new StartTime(startTime), new EndTime(endTime));
    }

    public static UnReachableTimePeriod of(StartTime startTime, EndTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        if (endTime.value().isBefore(startTime.value())) {
            throw new IllegalArgumentException("结束时间不得早于开始时间");
        }
        return new UnReachableTimePeriod(startTime, endTime);
    }

}
