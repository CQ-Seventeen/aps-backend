package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class OrderDemandOfMachineDTO {

    private int cylinderDiameter;

    private List<StylePartDemandDTO> demands;

    private double totalDays;
}