package com.santoni.iot.aps.application.resource.assembler;

import com.santoni.iot.aps.application.resource.dto.*;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrConfig;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrValue;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineOptions;
import com.santoni.iot.aps.domain.statistic.StatisticMachine;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MachineDTOAssembler {

    public OverviewMachineDTO assembleOverviewMachineDTO(StatisticMachine statistic) {
        var dto = new OverviewMachineDTO();
        dto.setTotalCount(statistic.getTotalCount().getValue());
        dto.setStatusCount(statistic.getStatusCount().entrySet()
                .stream().collect(Collectors.toMap(entry -> entry.getKey().getCode(),
                        entry -> entry.getValue().getValue())));
        dto.setTotalSeconds(statistic.getSaturation().getTotalSeconds());
        dto.setOccupiedSeconds(statistic.getSaturation().getTotalSeconds() -
                statistic.getSaturation().getAvailableSeconds());
        dto.setWorkloadSaturation(statistic.getSaturation().getSaturation());
        dto.setLowLoadCount(statistic.getLowLevelLoadCount());
        dto.setMediumLoadCount(statistic.getMediumLevelLoadCount());
        dto.setHighLoadCount(statistic.getHighLevelLoadCount());
        return dto;
    }

    public MachineDTO assembleMachineDTO(Machine machine) {
        var dto = new MachineDTO();
        fillMachineDTO(dto, machine);
        return dto;
    }

    public MachineListDTO assembleMachineListDTO(Machine machine) {
        var dto = new MachineListDTO();
        fillMachineDTO(dto, machine);
        if (null != machine.getHierarchy()) {
            var hierarchy = machine.getHierarchy();
            dto.setFactoryId(hierarchy.getFactory().getId().value());
            if (null != hierarchy.getFactory().getCode()) {{
                dto.setFactoryCode(hierarchy.getFactory().getCode().value());
            }}
            dto.setWorkshopId(hierarchy.getWorkshop().getId().value());
            if (null != hierarchy.getWorkshop().getCode()) {
                dto.setWorkshopCode(hierarchy.getWorkshop().getCode().value());
            }
            dto.setMachineGroupId(hierarchy.getMachineGroup().getId().value());
            if (null != hierarchy.getMachineGroup().getCode()) {{
                dto.setMachineGroupCode(hierarchy.getMachineGroup().getCode().value());
            }}
        }
        dto.setStatus(machine.getStatus().getCode());
        return dto;
    }

    private void fillMachineDTO(MachineDTO dto, Machine machine) {
        dto.setMachineId(machine.getId().value());
        dto.setDeviceId(machine.getDeviceId());
        dto.setMachineCode(machine.getCode());
        dto.setCylinderDiameter(machine.getMachineSize().getCylinderDiameter().value());
        dto.setNeedleSpacing(machine.getMachineSize().getNeedleSpacing().value());
        dto.setNeedleNumber(machine.getMachineSize().getNeedleNumber().value());
        dto.setMachineType(machine.getType());
        dto.setBareSpandexType(machine.getBareSpandexType());
        dto.setHighSpeed(machine.isHighSpeed());
        dto.setFeatures(machine.getFeatures().stream().map(this::assembleMachineFeatureDTO).toList());
    }

    public MachineOptionsDTO assembleMachineOptionsDTO(MachineOptions option) {
        var dto = new MachineOptionsDTO();
        dto.setCylinderDiameterMap(option.pairCylinderDiameterAndNeedleNumber());
        dto.setNeedleSpacingList(option.getNeedleSpacingList());
        dto.setMachineTypeList(option.getMachineTypeList());
        dto.setBareSpandexTypeList(option.getBareSpandexList());
        dto.setHighSpeedList(option.getHighSpeedList());
        dto.setMachineFeatureList(option.getFeatureList().stream().map(this::assembleMachineFeatureDTO).toList());
        dto.setMachineAreaList(option.getMachineAreaList());
        return dto;
    }

    public MachineAttrConfigDTO assembleMachineAttrConfigDTO(MachineAttrConfig config) {
        var dto = new MachineAttrConfigDTO();
        dto.setAttrCode(config.getAttr().code());
        dto.setOptionalValues(config.getOptionalValues().stream().map(MachineAttrValue::value).toList());
        dto.setOptionType(config.getOptionType().getCode());
        dto.setUseToFilter(config.useToFilter());
        return dto;
    }

    public MachineFeatureDTO assembleMachineFeatureDTO(MachineFeature feature) {
        var dto = new MachineFeatureDTO();
        dto.setAttrCode(feature.getAttr().code());
        dto.setAttrValues(feature.getValueList().stream().map(MachineAttrValue::value).toList());
        return dto;
    }
}
