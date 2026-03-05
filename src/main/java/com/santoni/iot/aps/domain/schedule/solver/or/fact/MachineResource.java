package com.santoni.iot.aps.domain.schedule.solver.or.fact;

import lombok.Data;

import java.util.List;

@Data
public class MachineResource {

    private long machineId;

    private long availableTime;

    private List<String> canWeavingStyles;

}
