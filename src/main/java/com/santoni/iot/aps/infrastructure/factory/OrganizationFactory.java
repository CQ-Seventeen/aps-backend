package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroupCode;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroupId;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryCode;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopCode;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteName;
import com.santoni.iot.aps.infrastructure.constant.OrgConstant;
import com.santoni.iot.aps.infrastructure.po.OrganizationPO;
import org.springframework.stereotype.Component;

@Component
public class OrganizationFactory {

    public OrganizationPO convertFactoryToOrganizationPO(FactoryCode factoryCode, long instituteId) {
        var po = new OrganizationPO();
        po.setCode(factoryCode.value());
        po.setParentId(instituteId);
        po.setLevel(OrgConstant.FACTORY_LEVEL);
        return po;
    }

    public OrganizationPO convertWorkshopToOrganizationPO(WorkshopCode workshopCode, long factoryId) {
        var po = new OrganizationPO();
        po.setCode(workshopCode.value());
        po.setParentId(factoryId);
        po.setLevel(OrgConstant.WORKSHOP_LEVEL);
        return po;
    }

    public OrganizationPO convertMachineGroupToOrganizationPO(MachineGroupCode machineGroupCode, long workshopId) {
        var po = new OrganizationPO();
        po.setCode(machineGroupCode.value());
        po.setParentId(workshopId);
        po.setLevel(OrgConstant.MACHINE_GROUP_LEVEL);
        return po;
    }

    public Factory composeFactoryFromPO(OrganizationPO po) {
        return Factory.of(new FactoryId(po.getId()), new FactoryCode(po.getCode()));
    }

    public Workshop composeWorkshopFromPO(OrganizationPO po) {
        return Workshop.of(new WorkshopId(po.getId()), new WorkshopCode(po.getCode()));
    }

    public MachineGroup composeMachineGroupFromPO(OrganizationPO po) {
        return MachineGroup.of(new MachineGroupId(po.getId()), new MachineGroupCode(po.getCode()));
    }

    public Institute composeInstituteFromPO(OrganizationPO po) {
        return Institute.of(new InstituteId(po.getId()), new InstituteName(po.getCode()));
    }
}
