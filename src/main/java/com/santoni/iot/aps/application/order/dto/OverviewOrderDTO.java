package com.santoni.iot.aps.application.order.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OverviewOrderDTO {

    private int unFinishedCount;

    private int unPlannedCount;

    private int unPlannedStyleCount;

    private int warningCount;
}
