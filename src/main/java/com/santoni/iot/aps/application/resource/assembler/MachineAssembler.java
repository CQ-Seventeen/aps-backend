package com.santoni.iot.aps.application.resource.assembler;

import com.santoni.iot.aps.application.resource.command.CreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.MachineFeatureCommand;
import com.santoni.iot.aps.application.resource.command.OperateMachineAttrConfigCommand;
import com.santoni.iot.aps.application.resource.query.PageMachineQuery;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleNumber;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import com.santoni.iot.aps.domain.support.entity.valueobj.OptionType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MachineAssembler {

    public MachineAttrConfig composeAttrConfigFromCmd(OperateMachineAttrConfigCommand cmd) {
        return new MachineAttrConfig(new MachineAttr(cmd.getAttrCode()),
                cmd.getAttrValueList().stream().map(MachineAttrValue::new).toList(),
                OptionType.findByCode(cmd.getOptionType()),
                cmd.isUseToFilter() ? UseToFilter.yes() : UseToFilter.no());
    }

    public Machine composeMachineFromCreateCmd(CreateMachineCommand cmd,
                                               Institute institute,
                                               MachineHierarchy hierarchy) {
        return new Machine(null,
                null,
                new MachineDeviceId(cmd.getDeviceId()),
                new MachineCode(cmd.getCode()),
                institute,
                hierarchy,
                MachineSize.machineSize(null == cmd.getCylinderDiameter() ? null : new CylinderDiameter(cmd.getCylinderDiameter()),
                        null == cmd.getNeedleSpacing() ? null : new NeedleSpacing(cmd.getNeedleSpacing()),
                        null == cmd.getNeedleNumber() ? null : new NeedleNumber(cmd.getNeedleNumber())),
                new MachineType(cmd.getMachineType()),
                new BareSpandex(cmd.getBareSpandexType()),
                cmd.isHighSpeed() ? HighSpeed.high() : HighSpeed.low(),
                CollectionUtils.isEmpty(cmd.getFeatures()) ? List.of() : cmd.getFeatures().stream().map(this::composeMachineFeature).toList(),
                MachineStatus.IDLE
        );
    }

    public SearchMachine composeSearchMachine(PageMachineQuery query) {
        return new SearchMachine(
                null == query.getFactoryId() ? null : new FactoryId(query.getFactoryId()),
                CollectionUtils.isNotEmpty(query.getFactoryIds()) ? query.getFactoryIds().stream().map(FactoryId::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getWorkshopIds()) ? query.getWorkshopIds().stream().map(WorkshopId::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getMachineGroupIds()) ? query.getMachineGroupIds().stream().map(MachineGroupId::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getStatusList()) ? query.getStatusList().stream().map(MachineStatus::getByCode).toList() : null,
                CollectionUtils.isNotEmpty(query.getCylinderDiameterList()) ? query.getCylinderDiameterList().stream().map(CylinderDiameter::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getNeedleSpacingList()) ? query.getNeedleSpacingList().stream().map(NeedleSpacing::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getNeedleNumberList()) ? query.getNeedleNumberList().stream().map(NeedleNumber::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getMachineTypeList()) ? query.getMachineTypeList().stream().map(MachineType::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getBareSpandexList()) ? query.getBareSpandexList().stream().map(BareSpandex::new).toList() : null,
                null == query.getHighSpeed() ? null : query.getHighSpeed() ? HighSpeed.high() : HighSpeed.low(),
                CollectionUtils.isNotEmpty(query.getFeatureList()) ? query.getFeatureList()
                        .stream().map(it -> MachineFeature.of(it.getAttrCode(), it.getAttrValueList())).toList() : null,
                CollectionUtils.isNotEmpty(query.getAreaList()) ? query.getAreaList().stream().map(MachineArea::new).toList() : null,
                query
        );
    }

    public MachineFeature composeMachineFeature(MachineFeatureCommand cmd) {
        return new MachineFeature(new MachineAttr(cmd.getAttrCode()),
                cmd.getAttrValueList().stream().map(MachineAttrValue::new).toList());
    }
}
