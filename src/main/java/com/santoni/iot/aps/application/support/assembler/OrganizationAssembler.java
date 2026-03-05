package com.santoni.iot.aps.application.support.assembler;

import com.santoni.iot.aps.application.support.dto.FactoryDTO;
import com.santoni.iot.aps.application.support.dto.MachineGroupDTO;
import com.santoni.iot.aps.application.support.dto.WorkshopDTO;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import org.springframework.stereotype.Component;

@Component
public class OrganizationAssembler {

    public FactoryDTO convertToFactoryDTO(Factory factory) {
        var dto = new FactoryDTO();
        dto.setFactoryId(factory.getId().value());
        dto.setFactoryCode(factory.getCode().value());
        return dto;
    }

    public WorkshopDTO convertToWorkshopDTO(Workshop workshop) {
        var dto = new WorkshopDTO();
        dto.setWorkshopId(workshop.getId().value());
        dto.setWorkshopCode(workshop.getCode().value());
        return dto;
    }

    public MachineGroupDTO convertToMachineGroupDTO(MachineGroup machineGroup) {
        var dto = new MachineGroupDTO();
        dto.setMachineGroupId(machineGroup.getId().value());
        dto.setMachineGroupCode(machineGroup.getCode().value());
        return dto;
    }
}
