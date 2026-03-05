package com.santoni.iot.aps.domain.plan.entity.valueobj;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.support.entity.DatePeriod;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MachineUsage {

    private Map<String, DailyMachineUsage> dailyUsageMap;

    private MachineUsage(Map<String, DailyMachineUsage> dailyUsageMap) {
        this.dailyUsageMap = dailyUsageMap;
    }

    public static MachineUsage initFromDatePeriods(List<DatePeriod> dateList) {
        Map<String, DailyMachineUsage> dailyUsageMap = Maps.newHashMapWithExpectedSize(dateList.size());
        for (var date : dateList) {
            dailyUsageMap.put(date.getDate(), DailyMachineUsage.empty(date.getDate()));
        }
        return new MachineUsage(dailyUsageMap);
    }

    public DailyMachineUsage getDailyUsage(String date) {
        return dailyUsageMap.get(date);
    }

    public void countOneInUse(String date) {
        var dailyUsage = dailyUsageMap.get(date);
        if (null == dailyUsage) {
            dailyUsageMap.put(date, DailyMachineUsage.oneInUse(date));
        } else {
            dailyUsage.oneInUse();
        }
    }

    public void countOne(String date) {
        var dailyUsage = dailyUsageMap.get(date);
        if (null == dailyUsage) {
            dailyUsageMap.put(date, DailyMachineUsage.oneInUse(date));
        } else {
            dailyUsage.one();
        }
    }
}
