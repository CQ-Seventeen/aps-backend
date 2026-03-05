package com.santoni.iot.aps.application.plan.dto.machine;

import lombok.Data;

import java.util.List;

@Data
public class AggregateMachinePlanDTO {

    private int cylinderDiameter;

    private List<MachinePlanDetailDTO> machinePlanList;
}
