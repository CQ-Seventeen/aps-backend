package com.santoni.iot.aps.domain.plan.entity.valueobj;

import lombok.Getter;

@Getter
public class DailyMachineUsage {

    private String date;

    private int count;

    private int inUseCount;

    private DailyMachineUsage(String date, int count, int inUseCount) {
        this.date = date;
        this.count = count;
        this.inUseCount = inUseCount;
    }

    public static DailyMachineUsage empty(String date) {
        return new DailyMachineUsage(date, 0, 0);
    }

    public static DailyMachineUsage oneInUse(String date) {
        return new DailyMachineUsage(date, 1, 1);
    }

    public static DailyMachineUsage oneNoUse(String date) {
        return new DailyMachineUsage(date, 1, 0);
    }

    public void oneInUse() {
        this.count++;
        this.inUseCount++;
    }

    public void one() {
        this.count++;
    }
}
