package com.santoni.iot.aps.domain.resource.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleNumber;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SearchMachine extends PageParam {

    @Getter
    private final FactoryId factoryId;

    private final List<FactoryId> factoryIds;
    
    private final List<WorkshopId> workshopIds;
    
    private final List<MachineGroupId> machineGroupIds;
    
    private final List<MachineStatus> statusList;

    private final List<CylinderDiameter> cylinderDiameterList;

    private final List<NeedleSpacing> needleSpacingList;

    private final List<NeedleNumber> needleNumberList;

    private final List<MachineType> machineTypeList;

    private final List<BareSpandex> bareSpandexList;

    private final HighSpeed highSpeed;

    @Getter
    private final List<MachineFeature> featureList;

    private final List<MachineArea> areaList;

    public SearchMachine(FactoryId factoryId,
                         List<FactoryId> factoryIds,
                         List<WorkshopId> workshopIds,
                         List<MachineGroupId> machineGroupIds,
                         List<MachineStatus> statusList,
                         List<CylinderDiameter> cylinderDiameterList,
                         List<NeedleSpacing> needleSpacingList,
                         List<NeedleNumber> needleNumberList,
                         List<MachineType> machineTypeList,
                         List<BareSpandex> bareSpandexList,
                         HighSpeed highSpeed,
                         List<MachineFeature> featureList,
                         List<MachineArea> areaList,
                         CommonPageQuery query) {
        this.factoryId = factoryId;
        this.factoryIds = factoryIds;
        this.workshopIds = workshopIds;
        this.machineGroupIds = machineGroupIds;
        this.statusList = statusList;
        this.cylinderDiameterList = cylinderDiameterList;
        this.needleSpacingList = needleSpacingList;
        this.needleNumberList = needleNumberList;
        this.machineTypeList = machineTypeList;
        this.bareSpandexList = bareSpandexList;
        this.highSpeed = highSpeed;
        this.featureList = featureList;
        this.areaList = areaList;
        fromPageQuery(query);
    }

    public SearchMachine(FactoryId factoryId,
                         List<FactoryId> factoryIds,
                         List<WorkshopId> workshopIds,
                         List<MachineGroupId> machineGroupIds,
                         List<MachineStatus> statusList,
                         List<CylinderDiameter> cylinderDiameterList,
                         List<NeedleSpacing> needleSpacingList,
                         List<NeedleNumber> needleNumberList,
                         List<MachineType> machineTypeList,
                         List<BareSpandex> bareSpandexList,
                         HighSpeed highSpeed,
                         List<MachineFeature> featureList,
                         List<MachineArea> areaList,
                         int page,
                         int pageSize) {
        this.factoryId = factoryId;
        this.factoryIds = factoryIds;
        this.workshopIds = workshopIds;
        this.machineGroupIds = machineGroupIds;
        this.statusList = statusList;
        this.cylinderDiameterList = cylinderDiameterList;
        this.needleSpacingList = needleSpacingList;
        this.needleNumberList = needleNumberList;
        this.machineTypeList = machineTypeList;
        this.bareSpandexList = bareSpandexList;
        this.highSpeed = highSpeed;
        this.featureList = featureList;
        this.areaList = areaList;
        setPage(page);
        setPageSize(pageSize);
    }

    public void nonPage() {
        this.setPageSize(-1);
        this.setPage(1);
    }

    public List<Long> getFactoryIds() {
        if (CollectionUtils.isEmpty(factoryIds)) {
            return List.of();
        }
        return factoryIds.stream().map(FactoryId::value).toList();
    }

    public List<Long> getWorkshopIds() {
        if (CollectionUtils.isEmpty(workshopIds)) {
            return List.of();
        }
        return workshopIds.stream().map(WorkshopId::value).toList();
    }

    public List<Long> getMachineGroupIds() {
        if (CollectionUtils.isEmpty(machineGroupIds)) {
            return List.of();
        }
        return machineGroupIds.stream().map(MachineGroupId::value).toList();
    }

    public List<Integer> getStatusList() {
        if (CollectionUtils.isEmpty(statusList)) {
            return List.of();
        }
        return statusList.stream().map(MachineStatus::getCode).toList();
    }

    public List<Integer> getCylinderDiameterList() {
        if (CollectionUtils.isEmpty(cylinderDiameterList)) {
            return List.of();
        }
        return cylinderDiameterList.stream().map(CylinderDiameter::value).toList();
    }

    public List<Integer> getNeedleSpacingList() {
        if (CollectionUtils.isEmpty(needleSpacingList)) {
            return List.of();
        }
        return needleSpacingList.stream().map(NeedleSpacing::value).toList();
    }

    public List<Integer> getNeedleNumberList() {
        if (CollectionUtils.isEmpty(needleNumberList)) {
            return List.of();
        }
        return needleNumberList.stream().map(NeedleNumber::value).toList();
    }

    public List<String> getMachineTypeList() {
        if (CollectionUtils.isEmpty(machineTypeList)) {
            return List.of();
        }
        return machineTypeList.stream().map(MachineType::value).toList();
    }

    public List<String> getBareSpandexList() {
        if (CollectionUtils.isEmpty(bareSpandexList)) {
            return List.of();
        }
        return bareSpandexList.stream().map(BareSpandex::value).toList();
    }

    public List<String> getAreaList() {
        if (CollectionUtils.isEmpty(areaList)) {
            return List.of();
        }
        return areaList.stream().map(MachineArea::value).toList();
    }

    public Boolean getHighSpeed() {
        return null == highSpeed ? null : highSpeed.value();
    }
}
