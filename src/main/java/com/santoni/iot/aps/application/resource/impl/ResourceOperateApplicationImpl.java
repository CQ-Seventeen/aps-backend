package com.santoni.iot.aps.application.resource.impl;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.resource.ResourceOperateApplication;
import com.santoni.iot.aps.application.resource.assembler.MachineAssembler;
import com.santoni.iot.aps.application.resource.command.BatchCreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.CreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.OperateMachineAttrConfigCommand;
import com.santoni.iot.aps.application.resource.command.UpdateMachineCommand;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.resource.repository.MachineAttrRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import com.santoni.iot.aps.infrastructure.threadpool.MachineOptionRefreshThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ResourceOperateApplicationImpl implements ResourceOperateApplication {

    @Autowired
    private ResourceDomainService resourceDomainService;

    @Autowired
    private MachineAttrRepository machineAttrRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MachineAssembler machineAssembler;

    @Override
    public BatchOperateResultDTO batchCreateMachine(BatchCreateMachineCommand cmd) {
        return null;
    }

    @Override
    public void createMachine(CreateMachineCommand command) {
        checkDeviceId(command.getDeviceId(), command.getFactoryId());
        var hierarchy = getHierarchy(command.getInstituteId(), command.getFactoryId(), command.getWorkshopId(), command.getMachineGroupId());
        var machine = machineAssembler.composeMachineFromCreateCmd(command, hierarchy.getLeft(), hierarchy.getRight());
        checkFeatures(machine.getFeatures());
        machineRepository.saveMachine(machine);
        submitRefreshOptionTask();
    }

    @Override
    public void updateMachine(UpdateMachineCommand command) {
        var machine = machineRepository.detailById(new MachineId(command.getMachineId()));
        if (null == machine) {
            throw new IllegalArgumentException("机器不存在");
        }
        var hierarchy = getHierarchy(command.getInstituteId(), command.getFactoryId(), command.getWorkshopId(), command.getMachineGroupId());
        List<MachineFeature> features = CollectionUtils.isEmpty(command.getFeatures()) ? Collections.emptyList() : command.getFeatures().stream().map(it -> machineAssembler.composeMachineFeature(it)).toList();
        machine.change(hierarchy.getRight(), new MachineType(command.getMachineType()),
                new BareSpandex(command.getBareSpandexType()), command.isHighSpeed() ? HighSpeed.high() : HighSpeed.low(), features);
        checkFeatures(machine.getFeatures());
        machineRepository.saveMachine(machine);
        submitRefreshOptionTask();
    }

    @Override
    public void createMachineAttr(OperateMachineAttrConfigCommand command) {
        var attrConfig = machineAssembler.composeAttrConfigFromCmd(command);
        var exist = machineAttrRepository.getConfigByAttr(attrConfig.getAttr());
        if (null != exist) {
            throw new IllegalArgumentException("机器属性已存在");
        }
        machineAttrRepository.createMachineAttrConfig(attrConfig);
        submitRefreshOptionTask();
    }

    @Override
    public void updateMachineAttr(OperateMachineAttrConfigCommand command) {
        var attrConfig = machineAssembler.composeAttrConfigFromCmd(command);
        var exist = machineAttrRepository.getConfigByAttr(attrConfig.getAttr());
        if (null == exist) {
            throw new IllegalArgumentException("机器属性不存在");
        }
        checkMachineAttrConfigCanModify(exist, attrConfig);
        machineAttrRepository.updateMachineAttrConfig(attrConfig);
        submitRefreshOptionTask();
    }

    private void checkMachineAttrConfigCanModify(MachineAttrConfig existConfig, MachineAttrConfig newConfig) {
        if (existConfig.multiSelect() && newConfig.singleSelect()) {
            throw new IllegalArgumentException("复选属性不允许修改为单选");
        }
        var deletedValues = resourceDomainService.deletedValues(existConfig, newConfig);
        if (CollectionUtils.isEmpty(deletedValues)) {
            return;
        }
        var options = new MachineOptions(List.of(), List.of(), List.of(), List.of(),
                Lists.newArrayList(new MachineFeature(existConfig.getAttr(), deletedValues)),
                List.of());
        var machineList = machineRepository.filterMachineByOption(options, null);
        if (CollectionUtils.isNotEmpty(machineList)) {
            throw new IllegalArgumentException("删除的属性值在使用中");
        }
    }

    private void checkFeatures(List<MachineFeature> featureList) {
        if (CollectionUtils.isEmpty(featureList)) {
            return;
        }
        var attrCodeList = featureList.stream().map(MachineFeature::getAttr).toList();
        var attrConfigList = machineAttrRepository.listConfigByAttr(attrCodeList);
        resourceDomainService.checkMachineFeature(attrConfigList, featureList);
    }

    private void checkDeviceId(String deviceId, long factoryId) {
        var exist = machineRepository.findByDeviceId(new FactoryId(factoryId), new MachineDeviceId(deviceId));
        if (null != exist) {
            throw new IllegalArgumentException("该机台号已存在");
        }
    }

    private Pair<Institute, MachineHierarchy> getHierarchy(long instituteId, long factoryId, long workshopId, long machineGroupId) {
        var institute = organizationRepository.getInstitute(new InstituteId(instituteId));
        if (null == institute) {
            throw new IllegalArgumentException("企业不存在");
        }
        var hierarchy = organizationRepository.getHierarchyByMachineGroupId(new MachineGroupId(machineGroupId));
        checkOrgHierarchy(hierarchy, workshopId, factoryId);
        return Pair.of(institute, hierarchy);
    }

    private void checkOrgHierarchy(MachineHierarchy hierarchy, long workshopId, long factoryId) {
        if (null == hierarchy) {
            throw new IllegalArgumentException("机组不存在");
        }
        if (null == hierarchy.getWorkshop() || hierarchy.getWorkshop().getId().value() != workshopId) {
            throw new IllegalArgumentException("车间不存在");
        }
        if (null == hierarchy.getFactory() || hierarchy.getFactory().getId().value() != factoryId) {
            throw new IllegalArgumentException("工厂不存在");
        }
    }

    private void submitRefreshOptionTask() {
        var instituteId = PlanContext.getInstituteId();
        MachineOptionRefreshThreadPool.submitRefreshTask(() -> machineRepository.refreshOptions(new InstituteId(instituteId)));
    }
}
