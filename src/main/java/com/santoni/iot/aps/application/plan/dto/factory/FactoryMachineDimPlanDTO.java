package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class FactoryMachineDimPlanDTO {

    private int cylinderDiameter;

    private double totalDays;

    private double availableDays;

    private double occupiedDays;

    private double orderOccupiedDays;

    private int machineNumber;

    private int openedNumber;

    private List<FactoryPlannedPeriodDTO> periods;
}