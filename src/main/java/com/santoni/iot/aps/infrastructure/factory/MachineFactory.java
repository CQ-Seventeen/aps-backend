package com.santoni.iot.aps.infrastructure.factory;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.*;
import com.santoni.iot.aps.infrastructure.po.MachineAttrConfigPO;
import com.santoni.iot.aps.infrastructure.po.MachinePO;
import com.santoni.iot.aps.infrastructure.po.MesMachinePO;
import com.santoni.iot.aps.infrastructure.po.assistance.MesSizePO;
import com.santoni.iot.aps.infrastructure.po.assistance.SizePO;
import com.santoni.iot.aps.infrastructure.po.qo.MachineSizeQO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class MachineFactory {

    public MachineSizeQO convertSizeToMachineSizeQO(MachineSize machineSize) {
        var qo = new MachineSizeQO();
        if (null != machineSize.getCylinderDiameter()) {
            qo.setCylinderDiameter(machineSize.getCylinderDiameter().value());
        }
        if (null != machineSize.getNeedleSpacing()) {
            qo.setNeedleSpacing(machineSize.getNeedleSpacing().value());
        }
        return qo;
    }

    public MachinePO convertMachineToPO(Machine machine) {
        var po = new MachinePO();
        po.setDeviceId(machine.getDeviceId());
        po.setCode(machine.getCode());
        po.setStatus(machine.getStatus().getCode());
        po.setInstituteId(machine.getInstitute().getId());
        if (null != machine.getHierarchy()) {
            var hierarchy = machine.getHierarchy();
            if (null != hierarchy.getFactory()) {
                po.setFactoryId(hierarchy.getFactory().getId().value());
            }
            if (null != hierarchy.getWorkshop()) {
                po.setWorkshopId(hierarchy.getWorkshop().getId().value());
            }
            if (null != hierarchy.getMachineGroup()) {
                po.setMachineGroupId(hierarchy.getMachineGroup().getId().value());
            }
        }
        po.setCylinderDiameter(machine.getMachineSize().getCylinderDiameter().value());
        po.setNeedleSpacing(machine.getMachineSize().getNeedleSpacing().value());
        po.setNeedleNumber(machine.getMachineSize().getNeedleNumber().value());
        po.setMachineType(machine.getType());
        po.setBareSpandexType(machine.getBareSpandexType());
        po.setHighSpeed(machine.isHighSpeed());
        po.setFeatures(convertFeaturesToMap(machine.getFeatures()));
        po.setStatus(machine.getStatus().getCode());
        return po;
    }

    public Machine composeMachineEntityFromPO(MachinePO po,
                                              Factory factory,
                                              Workshop workshop,
                                              MachineGroup machineGroup) {
        var hierarchy = null != factory && null != workshop && null != machineGroup ? new MachineHierarchy(factory, workshop, machineGroup)
                : new MachineHierarchy(
                        Factory.of(new FactoryId(po.getFactoryId())),
                        Workshop.of(new WorkshopId(po.getWorkshopId())),
                        MachineGroup.of(new MachineGroupId(po.getMachineGroupId())));
        return new Machine(new MachineId(po.getId()),
                null,
                new MachineDeviceId(po.getDeviceId()),
                new MachineCode(po.getCode()),
                Institute.of(new InstituteId(po.getInstituteId())),
                hierarchy,
                MachineSize.machineSize(new CylinderDiameter(po.getCylinderDiameter()),
                        new NeedleSpacing(po.getNeedleSpacing()),
                        new NeedleNumber(po.getNeedleNumber())),
                StringUtils.isBlank(po.getMachineType()) ? null : new MachineType(po.getMachineType()),
                StringUtils.isBlank(po.getBareSpandexType()) ? null : new BareSpandex(po.getBareSpandexType()),
                po.isHighSpeed() ? HighSpeed.high() : HighSpeed.low(),
                composeMachineFeature(po.getFeatures()),
                MachineStatus.getByCode(po.getStatus())
        );
    }

    public MachineSize composeMachineSize(SizePO po) {
        return MachineSize.machineSize(new CylinderDiameter(po.getCylinderDiameter()),
                new NeedleSpacing(po.getNeedleSpacing()),
                new NeedleNumber(po.getNeedleNumber()));
    }

    public MachineAttrConfig composeMachineAttrConfig(MachineAttrConfigPO po) {
        return new MachineAttrConfig(
                new MachineAttr(po.getAttrCode()),
                JacksonUtil.readAsObjList(po.getOptionalValues(), String.class).stream().map(MachineAttrValue::new).toList(),
                OptionType.findByCode(po.getOptionType()),
                po.isUseToFilter() ? UseToFilter.yes() : UseToFilter.no()
        );
    }

    public MachineAttrConfigPO convertMachineAttrConfigToPO(MachineAttrConfig config) {
        var po = new MachineAttrConfigPO();
        po.setInstituteId(PlanContext.getInstituteId());
        po.setAttrCode(config.getAttr().code());
        po.setOptionalValues(JacksonUtil.toJson(config.getOptionalValues().stream().map(MachineAttrValue::value).toList()));
        po.setOptionType(config.getOptionType().getCode());
        po.setUseToFilter(config.useToFilter());
        return po;
    }

    public MachineFeature composeMachineFeatureFromConfig(MachineAttrConfigPO po) {
        return new MachineFeature(
                new MachineAttr(po.getAttrCode()),
                JacksonUtil.readAsObjList(po.getOptionalValues(), String.class).stream().map(MachineAttrValue::new).toList()
        );
    }

    public Map<String, List<String>> convertFeaturesToMap(List<MachineFeature> featureList) {
        if (CollectionUtils.isEmpty(featureList)) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> result = Maps.newHashMapWithExpectedSize(featureList.size());
        for (var feature : featureList) {
            result.put(feature.getAttr().code(), feature.getValueList().stream().map(MachineAttrValue::value).toList());
        }
        return result;
    }

    public List<MachineFeature> composeMachineFeature(Map<String, List<String>> featureMap) {
        if (MapUtils.isEmpty(featureMap)) {
            return Collections.emptyList();
        }
        return featureMap.entrySet().stream()
                .map(it -> new MachineFeature(new MachineAttr(it.getKey()),
                        it.getValue().stream().map(MachineAttrValue::new).toList())).toList();
    }

    public Machine composeMachineFromMesPO(MesMachinePO po) {
        return new Machine(new MachineId(po.getId()),
                new OuterMachineId(po.getOuterId()),
                new MachineDeviceId(po.getMachineCode()),
                new MachineCode(po.getMachineNumber()),
                null,
                null,
                MachineSize.machineSize(new CylinderDiameter(Integer.parseInt(po.getCylinderDiameter().split("寸")[0])),
                        po.getNeedleSpacing().contains("G") ? new NeedleSpacing(Integer.parseInt(po.getNeedleSpacing().split("G")[0])) : new NeedleSpacing(10),
                        null),
                StringUtils.isBlank(po.getMachineType()) ? null : new MachineType(po.getMachineType()),
                null,
                HighSpeed.low(),
                Collections.emptyList(),
                MachineStatus.RUNNING
        );
    }

    public MachineSize composeMachineSize(MesSizePO po) {
        if (po.getNeedleSpacing().contains("UKN0:TR1")) {
            return null;
        }
        return MachineSize.machineSize(new CylinderDiameter(Integer.parseInt(po.getCylinderDiameter().split("寸")[0])),
                new NeedleSpacing(Integer.parseInt(po.getNeedleSpacing().split("G")[0])), null);
    }
}
