package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DatePeriod {

    private final StartTime start;

    private final EndTime end;

    private final String date;

    private DatePeriod(StartTime start, EndTime end, String date) {
        this.start = start;
        this.end = end;
        this.date = date;
    }

    public static DatePeriod of(String date, LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime || null == endTime) {
            throw new IllegalArgumentException("起止时间必须指定");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不得早于开始时间");
        }
        return new DatePeriod(new StartTime(startTime), new EndTime(endTime), date);
    }

}
