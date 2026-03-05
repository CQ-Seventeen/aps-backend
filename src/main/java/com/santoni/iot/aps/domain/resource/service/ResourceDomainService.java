package com.santoni.iot.aps.domain.resource.service;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrConfig;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrValue;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineOptions;
import com.santoni.iot.aps.domain.statistic.StatisticMachine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ResourceDomainService {

    MachineOptions extractMachineOptions(List<MachinePlan> machineList);

    Map<Long, List<StyleComponent>> pairMachineAndStyle(List<Machine> machineList, List<StyleSku> styleList);

    void checkMachineFeature(List<MachineAttrConfig> configList, List<MachineFeature> featureList);

    List<MachineAttrValue> deletedValues(MachineAttrConfig exist, MachineAttrConfig newConfig);

    StatisticMachine statisticMachine(List<MachinePlan> machineList, LocalDateTime start, LocalDateTime end);

    MachineOptions compatibleMachineByStyleComponent(StyleComponent component);

    MachineOptions compatibleMachineByComponentList(List<StyleComponent> componentList);
}
