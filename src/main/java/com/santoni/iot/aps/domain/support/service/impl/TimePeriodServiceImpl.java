package com.santoni.iot.aps.domain.support.service.impl;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import com.santoni.iot.aps.domain.support.entity.DatePeriod;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimePeriodServiceImpl implements TimePeriodService {

    @Override
    public AvailableTime calculateAvailableTime(List<TimePeriod> timePeriods, LocalDateTime start, LocalDateTime end) {
        if (CollectionUtils.isEmpty(timePeriods)) {
            return new AvailableTime(start, end);
        }
        List<TimePeriod> result = Lists.newArrayList();

        int startIndex = findFirstIndex(timePeriods, start);
        handleIdleDurationBeforeFirstPeriod(timePeriods.get(startIndex), start, result);
        handleIdleDurationBetweenRestPeriods(timePeriods, startIndex, end, result);
        handleIdleDurationAfterLastSegment(timePeriods, end, result);

        return new AvailableTime(result, start, end);
    }

    @Override
    public List<DatePeriod> extractDate(LocalDateTime start, LocalDateTime end) {
        List<DatePeriod> result = Lists.newArrayList();
        while (start.isBefore(end)) {
            var startTime = TimeUtil.getStartOf(start);
            var endTime = TimeUtil.getEndOf(start);
            var date = TimeUtil.formatYYYYMMDD(start);
            result.add(DatePeriod.of(date, startTime, endTime));
            start = start.plusDays(1);
        }
        return result;
    }

    @Override
    public List<Long> calculateAvailableTimeByPeriod(List<TimePeriod> timePeriods,
                                                     LocalDateTime start,
                                                     List<LocalDateTime> endTimes) {
        if (CollectionUtils.isEmpty(timePeriods)) {
            return endTimes.stream().map(end -> Duration.between(start, end).toSeconds()).collect(Collectors.toList());
        }
        if (timePeriods.get(timePeriods.size() - 1).getEnd().value().isBefore(start)) {
            return endTimes.stream().map(end -> Duration.between(start, end).toSeconds()).collect(Collectors.toList());
        }
        List<Long> result = Lists.newArrayListWithExpectedSize(endTimes.size());

        int index = 0;
        LocalDateTime startTime = start;

        var firstRes = findAvailableTime(index, timePeriods, startTime, endTimes.get(0));
        result.add(firstRes.getLeft());
        startTime = firstRes.getMiddle();
        index = firstRes.getRight();

        for (int i = 1; i < endTimes.size(); i++) {
            var res = findAvailableTime(index, timePeriods, startTime, endTimes.get(i));
            result.add(res.getLeft());
            startTime = res.getMiddle();
            index = res.getRight();
        }
        return result;
    }

    private Triple<Long, LocalDateTime, Integer> findAvailableTime(int startIndex,
                                                                   List<TimePeriod> timePeriods,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end) {
        long availableTime = 0;
        int index = startIndex;
        while (index < timePeriods.size()) {
            var curPeriod = timePeriods.get(index);
            if (start.isBefore(curPeriod.getStart().value())) {
                if (end.isBefore(curPeriod.getStart().value())) {
                    return Triple.of(Duration.between(start, end).toSeconds(), end, index);
                }
                if (end.isBefore(curPeriod.getEnd().value())) {
                    return Triple.of(Duration.between(start, curPeriod.getStart().value()).toSeconds(), end, index);
                }
                availableTime += Duration.between(start, curPeriod.getStart().value()).toSeconds();
                start = curPeriod.getEnd().value();
            } else {
                if (end.isBefore(curPeriod.getEnd().value())) {
                    return Triple.of(0L, end, index);
                }
                start = curPeriod.getStart().value();
            }
            index++;
        }
        return Triple.of(availableTime, end, index);
    }

    private int findFirstIndex(List<TimePeriod> timePeriods, LocalDateTime start) {
        int index = 0;
        while (true) {
            var prevEndTime = timePeriods.get(index).getEnd().value();
            if (prevEndTime.isBefore(start)) {
                index++;
                continue;
            }
            break;
        }
        return index;
    }

    private void handleIdleDurationBeforeFirstPeriod(TimePeriod timePeriod, LocalDateTime start, List<TimePeriod> result) {
        var firstStart = timePeriod.getStart().value();
        if (firstStart.isAfter(start) && !firstStart.equals(start)) {
            result.add(TimePeriod.of(start, firstStart));
        }
    }

    private void handleIdleDurationBetweenRestPeriods(List<TimePeriod> allPeriods, int index, LocalDateTime endTime, List<TimePeriod> result) {
        for (int i = index; i < allPeriods.size() - 1; i++) {
            var prevEndTime = allPeriods.get(i).getEnd().value();
            if (prevEndTime.isAfter(endTime)) {
                break;
            }
            var nextStartTime = allPeriods.get(i + 1).getStart().value();
            if (nextStartTime.isAfter(endTime)) {
                if (Duration.between(prevEndTime, endTime).getSeconds() > 300) {
                    result.add(TimePeriod.of(prevEndTime, endTime));
                }
                break;
            }
            if (Duration.between(prevEndTime, nextStartTime).getSeconds() > 300) {
                result.add(TimePeriod.of(prevEndTime, nextStartTime));
            }
        }
    }

    private void handleIdleDurationAfterLastSegment(List<TimePeriod> allPeriods, LocalDateTime endTime, List<TimePeriod> result) {
        var lastSeg = allPeriods.get(allPeriods.size() - 1);
        if (lastSeg.getEnd().value().isBefore(endTime)) {
            result.add(TimePeriod.of(lastSeg.getEnd().value(), endTime));
        }
    }
}
