package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OverviewMachineDTO {

    private int totalCount;

    private Map<Integer, Integer> statusCount;

    private long totalSeconds;

    private long occupiedSeconds;

    private double workloadSaturation;

    private int lowLoadCount;

    private int mediumLoadCount;

    private int highLoadCount;
}
