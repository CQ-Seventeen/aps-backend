package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class FactoryOrderDimPlanDTO {

    private String startTime;

    private String endTime;

    private ProduceOrderDemandDTO orderDemand;

    private List<FactoryMachineDimPlanDTO> machineDimPlanList;
}