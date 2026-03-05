package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.common.utils.TimeUtil;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExecuteTime {

    private LocalDateTime value;

    private String date;

    private String hour;

    public ExecuteTime(LocalDateTime value) {
        this.value = value;
        this.date = TimeUtil.formatYYYYMMDD(value);
        this.hour = TimeUtil.formatHour(value);
    }
}
