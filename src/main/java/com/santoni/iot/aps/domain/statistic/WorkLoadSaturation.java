package com.santoni.iot.aps.domain.statistic;

import lombok.Getter;

@Getter
public class WorkLoadSaturation {

    private long unitSeconds;

    private long totalSeconds;

    private long availableSeconds;

    private double saturation;

    public WorkLoadSaturation(long unitSeconds) {
        this.unitSeconds = unitSeconds;
    }

    public void countOne(long availableSeconds) {
        this.totalSeconds += unitSeconds;
        this.availableSeconds += availableSeconds;
    }

    public void calculateSaturation() {
        this.saturation = (double) (totalSeconds - availableSeconds) / totalSeconds;
    }

}
