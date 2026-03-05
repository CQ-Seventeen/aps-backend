package com.santoni.iot.aps.domain.resource.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.statistic.StatisticMachine;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.entity.valueobj.OptionType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceDomainServiceImpl implements ResourceDomainService {

    @Override
    public MachineOptions extractMachineOptions(List<MachinePlan> machineList) {
        Set<String> machineTypeSet = Sets.newHashSet();
        Set<String> bareSpandexSet = Sets.newHashSet();
        Set<Boolean> highSpeedSet = Sets.newHashSet();

        for (var machine : machineList) {
            machineTypeSet.add(machine.getMachine().getType());
            bareSpandexSet.add(machine.getMachine().getBareSpandexType());
            highSpeedSet.add(machine.getMachine().isHighSpeed());
        }
        return new MachineOptions(List.of(), machineTypeSet.stream().map(MachineType::new).toList(),
                bareSpandexSet.stream().map(BareSpandex::new).toList(),
                highSpeedSet.stream().map(HighSpeed::new).toList(),
                List.of(),
                List.of());
    }

    @Override
    public Map<Long, List<StyleComponent>> pairMachineAndStyle(List<Machine> machineList, List<StyleSku> styleList) {
        var styleMap = styleList.stream().map(StyleSku::getComponents).flatMap(List::stream)
                .collect(Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value(),
                Collectors.groupingBy(it -> it.getMachineSize().getNeedleSpacing().value())));
        Map<Long, List<StyleComponent>> result = Maps.newHashMapWithExpectedSize(machineList.size());
        for (var machine : machineList) {
            var needleNumberMap = styleMap.get(machine.getMachineSize().getCylinderDiameter().value());
            if (MapUtils.isEmpty(needleNumberMap)) {
                continue;
            }
            result.put(machine.getId().value(), needleNumberMap.get(machine.getMachineSize().getNeedleSpacing().value()));
        }
        return result;
    }

    @Override
    public void checkMachineFeature(List<MachineAttrConfig> configList, List<MachineFeature> featureList) {
        var configMap = configList.stream()
                .collect(Collectors.toMap(it -> it.getAttr().code(),
                        it -> it,
                        (v1, v2) -> v1));
        for (var feature : featureList) {
            var config = configMap.get(feature.getAttr().code());
            if (null == config) {
                throw new IllegalArgumentException("机器属性不存在:" + feature.getAttr().code());
            }
            if (config.getOptionType() == OptionType.SINGLE && feature.getValueList().size() > 1) {
                throw new IllegalArgumentException("属性值只可单选:" + feature.getAttr().code());
            }
            var valueSet = config.getOptionalValues().stream().map(MachineAttrValue::value).collect(Collectors.toSet());
            for (var attrValue : feature.getValueList()) {
                if (!valueSet.contains(attrValue.value())) {
                    throw new IllegalArgumentException("属性值不合法:" + attrValue.value());
                }
            }
        }
    }

    @Override
    public List<MachineAttrValue> deletedValues(MachineAttrConfig exist, MachineAttrConfig newConfig) {
        var newValueSets = newConfig.getOptionalValues().stream().map(MachineAttrValue::value).collect(Collectors.toSet());
        List<MachineAttrValue> result = Lists.newArrayList();
        for (var attrValue : exist.getOptionalValues()) {
            if (!newValueSets.contains(attrValue.value())) {
                result.add(attrValue);
            }
        }
        return result;
    }

    @Override
    public StatisticMachine statisticMachine(List<MachinePlan> machineList, LocalDateTime start, LocalDateTime end) {
        var statistic = new StatisticMachine(TimePeriod.of(start, end));

        for (var plan : machineList) {
            statistic.countOneMachine(plan);
        }
        statistic.getSaturation().calculateSaturation();
        return statistic;
    }

    @Override
    public MachineOptions compatibleMachineByStyleComponent(StyleComponent component) {
        var result = MachineOptions.empty();
        result.unionRequirement(component.getRequirement());
        result.unionMachineSize(component.getMachineSize());
        return result;
    }

    @Override
    public MachineOptions compatibleMachineByComponentList(List<StyleComponent> componentList) {
        var result = MachineOptions.empty();
        for (var component : componentList) {
            result.unionRequirement(component.getRequirement());
            result.unionMachineSize(component.getMachineSize());
        }
        return result;
    }
}
