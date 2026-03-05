package com.santoni.iot.aps.domain.support.service;

import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import com.santoni.iot.aps.domain.support.entity.DatePeriod;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;

import java.time.LocalDateTime;
import java.util.List;

public interface TimePeriodService {

    AvailableTime calculateAvailableTime(List<TimePeriod> timePeriods, LocalDateTime start, LocalDateTime end);

    List<DatePeriod> extractDate(LocalDateTime start, LocalDateTime end);

    List<Long> calculateAvailableTimeByPeriod(List<TimePeriod> timePeriods, LocalDateTime start, List<LocalDateTime> endTimes);
}
