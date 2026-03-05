package com.santoni.iot.aps.domain.support.repository;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroupId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineHierarchy;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;

import java.util.List;

public interface OrganizationRepository {

    Institute getInstitute(InstituteId instituteId);

    MachineHierarchy getHierarchyByMachineGroupId(MachineGroupId groupId);

    List<Factory> listFactoryByInstitute(InstituteId instituteId);

    List<Workshop> listWorkshopByFactory(FactoryId factoryId);

    List<MachineGroup> listMachineGroupByWorkShop(WorkshopId workshopId);

    List<Factory> listAllFactory();

    Factory getFactoryById(FactoryId factoryId);

    List<Institute> listAllInstitute();

}
