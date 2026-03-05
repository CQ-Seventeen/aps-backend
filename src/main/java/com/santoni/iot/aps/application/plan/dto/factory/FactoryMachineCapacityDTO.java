package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

@Data
public class FactoryMachineCapacityDTO {

    private int cylinderDiameter;

    private long factoryId;

    private String factoryCode;

    private int machineNumber;

    private int openedMachineNumber;

    private double totalMachineDays;

    private double occupiedMachineDays;

    private double availableMachineDays;
}
