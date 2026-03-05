package com.santoni.iot.aps.domain.execute.entity.valueobj;

import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProducePeriod {

    private final StartTime start;

    private EndTime end;

    private ProducePeriod(StartTime start, EndTime end) {
        this.start = start;
        this.end = end;
    }

    public static ProducePeriod of(LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime) {
            throw new IllegalArgumentException("执行开始时间必须指定");
        }
        return new ProducePeriod(new StartTime(startTime), null == endTime ? null : new EndTime(endTime));
    }

    public void end() {
        this.end = new EndTime(LocalDateTime.now());
    }

}
