package com.santoni.iot.aps.domain.support.entity.valueobj;

import com.santoni.iot.aps.domain.plan.constant.PlanConstant;

public class MachineDays {

    private long seconds;

    private double value;

    private MachineDays(long seconds, double value) {
        this.seconds = seconds;
        this.value = value;
    }

    public static MachineDays of(double value) {
        if (Double.compare(value, 0.0) < 0) {
            throw new IllegalArgumentException("机器天数必须不小于0");
        }
        return new MachineDays(0, value);
    }

    public static MachineDays fromSeconds(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("机器天数必须不小于0");
        }
        return new MachineDays(seconds, 0.0);
    }

    public void add(double days) {
        this.value += days;
    }

    public void addSeconds(long seconds) {
        this.seconds += seconds;
    }

    public double getDays() {
        if (seconds > 0) {
            if (Double.compare(value, 0.0) <= 0) {
                this.value = Math.ceil(((double) seconds / PlanConstant.SECONDS_PER_DAY) * 10) / 10;
            }
        }
        return value;
    }
}
