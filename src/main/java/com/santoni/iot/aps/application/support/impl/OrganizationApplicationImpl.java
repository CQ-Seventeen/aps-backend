package com.santoni.iot.aps.application.support.impl;

import com.santoni.iot.aps.application.support.OrganizationApplication;
import com.santoni.iot.aps.application.support.assembler.OrganizationAssembler;
import com.santoni.iot.aps.application.support.dto.FactoryDTO;
import com.santoni.iot.aps.application.support.dto.MachineGroupDTO;
import com.santoni.iot.aps.application.support.dto.WorkshopDTO;
import com.santoni.iot.aps.application.support.query.ListFactoryQuery;
import com.santoni.iot.aps.application.support.query.ListMachineGroupQuery;
import com.santoni.iot.aps.application.support.query.ListWorkshopQuery;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrganizationApplicationImpl implements OrganizationApplication {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationAssembler organizationAssembler;

    @Override
    public List<FactoryDTO> listFactoryByInstitute(ListFactoryQuery query) {
        var factoryList = organizationRepository.listFactoryByInstitute(new InstituteId(query.getInstituteId()));
        if (CollectionUtils.isEmpty(factoryList)) {
            return List.of();
        }
        return factoryList.stream().map(it -> organizationAssembler.convertToFactoryDTO(it)).toList();
    }

    @Override
    public List<WorkshopDTO> listWorkshopByFactory(ListWorkshopQuery query) {
        var workshopList = organizationRepository.listWorkshopByFactory(new FactoryId(query.getFactoryId()));
        if (CollectionUtils.isEmpty(workshopList)) {
            return List.of();
        }
        return workshopList.stream().map(it -> organizationAssembler.convertToWorkshopDTO(it)).toList();
    }

    @Override
    public List<MachineGroupDTO> listMachineGroupByWorkshop(ListMachineGroupQuery query) {
        var machineGroupList = organizationRepository.listMachineGroupByWorkShop(new WorkshopId(query.getWorkshopId()));
        if (CollectionUtils.isEmpty(machineGroupList)) {
            return List.of();
        }
        return machineGroupList.stream().map(it -> organizationAssembler.convertToMachineGroupDTO(it)).toList();
    }
}
