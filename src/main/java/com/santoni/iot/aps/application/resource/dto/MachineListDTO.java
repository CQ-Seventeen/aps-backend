package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachineListDTO extends MachineDTO {

    private long factoryId;

    private String factoryCode;

    private long workshopId;

    private String workshopCode;

    private long machineGroupId;

    private String machineGroupCode;

    private int status;
}
