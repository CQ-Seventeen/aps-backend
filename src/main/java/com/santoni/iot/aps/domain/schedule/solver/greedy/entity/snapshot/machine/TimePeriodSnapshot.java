package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.machine;

import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TimePeriodSnapshot {

    private LocalDateTime start;

    private LocalDateTime end;

    private double totalTime;

    public TimePeriod toTimePeriod() {
        this.totalTime = 0;
        return TimePeriod.of(start, end);
    }

    public TimePeriod splitBy(LocalDateTime endTime) {
        var timePeriod = TimePeriod.of(start, endTime);
        this.start = endTime;
        this.totalTime = Duration.between(endTime, end).toSeconds();
        return timePeriod;
    }

    public TimePeriod occupyTime(double time) {
        var endTime = start.plusSeconds((long) Math.ceil((time)));
        var timePeriod = TimePeriod.of(start, endTime);
        this.start = endTime;
        this.totalTime -= time;
        return timePeriod;
    }

}
