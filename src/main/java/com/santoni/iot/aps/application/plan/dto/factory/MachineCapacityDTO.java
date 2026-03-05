package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class MachineCapacityDTO {

    private int cylinderDiameter;

    private int machineNumber;

    private int openedMachineNumber;

    private double totalMachineDays;

    private double occupiedMachineDays;

    private double availableMachineDays;

    private List<FactoryMachineCapacityDTO> factoryCapacityList;
}
