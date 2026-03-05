package com.santoni.iot.aps.infrastructure.repository;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroupId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineHierarchy;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import com.santoni.iot.aps.infrastructure.constant.OrgConstant;
import com.santoni.iot.aps.infrastructure.database.aps.OrganizationMapper;
import com.santoni.iot.aps.infrastructure.factory.OrganizationFactory;
import com.santoni.iot.aps.infrastructure.po.OrganizationPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private OrganizationFactory organizationFactory;

    @Override
    public Institute getInstitute(InstituteId instituteId) {
        var po = organizationMapper.getById(instituteId.value());
        if (null == po) {
            return null;
        }
        return organizationFactory.composeInstituteFromPO(po);
    }

    @Override
    public MachineHierarchy getHierarchyByMachineGroupId(MachineGroupId groupId) {
        var path = organizationMapper.pathFromLeaf(groupId.value());
        if (CollectionUtils.isEmpty(path)) {
            return null;
        }
        return buildMachineHierarchyByPath(path);
    }

    @Override
    public List<Factory> listFactoryByInstitute(InstituteId instituteId) {
        var poList = organizationMapper.listChildren(instituteId.value());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> organizationFactory.composeFactoryFromPO(it)).toList();
    }

    @Override
    public List<Workshop> listWorkshopByFactory(FactoryId factoryId) {
        var poList = organizationMapper.listChildren(factoryId.value());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> organizationFactory.composeWorkshopFromPO(it)).toList();
    }

    @Override
    public List<MachineGroup> listMachineGroupByWorkShop(WorkshopId workshopId) {
        var poList = organizationMapper.listChildren(workshopId.value());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> organizationFactory.composeMachineGroupFromPO(it)).toList();
    }

    @Override
    public List<Factory> listAllFactory() {
        var poList = organizationMapper.listChildren(PlanContext.getInstituteId());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> organizationFactory.composeFactoryFromPO(it)).toList();
    }

    @Override
    public Factory getFactoryById(FactoryId factoryId) {
        var po = organizationMapper.getById(factoryId.value());
        if (null == po) {
            return null;
        }
        return organizationFactory.composeFactoryFromPO(po);
    }

    @Override
    public List<Institute> listAllInstitute() {
        var poList = organizationMapper.listAll(OrgConstant.INSTITUTE_LEVEL);
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> organizationFactory.composeInstituteFromPO(it)).toList();
    }

    private MachineHierarchy buildMachineHierarchyByPath(List<OrganizationPO> path) {
        Factory factory = null;
        Workshop workshop = null;
        MachineGroup machineGroup = null;
        for (var po : path) {
            if (po.getLevel() == OrgConstant.FACTORY_LEVEL) {
                factory = organizationFactory.composeFactoryFromPO(po);
            }
            if (po.getLevel() == OrgConstant.WORKSHOP_LEVEL) {
                workshop = organizationFactory.composeWorkshopFromPO(po);
            }
            if (po.getLevel() == OrgConstant.MACHINE_GROUP_LEVEL) {
                machineGroup = organizationFactory.composeMachineGroupFromPO(po);
            }
        }
        return new MachineHierarchy(factory, workshop, machineGroup);
    }
}
