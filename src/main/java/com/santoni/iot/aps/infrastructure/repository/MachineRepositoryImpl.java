package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.infrastructure.database.aps.MachineAttrConfigMapper;
import com.santoni.iot.aps.infrastructure.database.aps.MachineMapper;
import com.santoni.iot.aps.infrastructure.database.aps.OrganizationMapper;
import com.santoni.iot.aps.infrastructure.database.mes.MesMachineMapper;
import com.santoni.iot.aps.infrastructure.factory.MachineFactory;
import com.santoni.iot.aps.infrastructure.factory.OrganizationFactory;
import com.santoni.iot.aps.infrastructure.po.MachineAttrConfigPO;
import com.santoni.iot.aps.infrastructure.po.MachinePO;
import com.santoni.iot.aps.infrastructure.po.MesMachinePO;
import com.santoni.iot.aps.infrastructure.po.qo.MachineAttrQO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchMachineQO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class MachineRepositoryImpl implements MachineRepository {

    @Autowired
    private MachineMapper machineMapper;

    @Autowired
    private MesMachineMapper mesMachineMapper;

    @Autowired
    private MachineAttrConfigMapper machineAttrConfigMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private MachineFactory machineFactory;

    @Autowired
    private OrganizationFactory organizationFactory;

    private final LoadingCache<Long, MachineOptions> optionsCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Long, MachineOptions>() {
                @NotNull
                @Override
                public MachineOptions load(@NotNull Long key) throws Exception {
                    return loadMachineOptions(key);
                }
            });

    @Override
    public void saveMachine(Machine machine) {
        var po = machineFactory.convertMachineToPO(machine);
        var userId = PlanContext.getUserId();
        if (null == machine.getId()) {
            po.setCreatorId(userId);
            po.setOperatorId(userId);
            machineMapper.insert(po);
        } else {
            po.setId(machine.getId().value());
            po.setOperatorId(userId);
            machineMapper.update(po);
        }
    }

    @Override
    public void batchSaveMachines(List<Machine> machines) {

    }

    @Override
    public List<Machine> listMachineById(Collection<MachineId> machineIdList) {
        var poList = mesMachineMapper.listById(machineIdList.stream().map(MachineId::value).toList());
        return composeMachineListFromMes(poList);
    }

    @Override
    public Machine findByDeviceId(FactoryId factoryId, MachineDeviceId deviceId) {
        var po = mesMachineMapper.getByDeviceId(deviceId.value());
        if (po == null) {
            return null;
        }
        return machineFactory.composeMachineFromMesPO(po);
    }

    @Override
    public Machine detailById(MachineId machineId) {
        var po = mesMachineMapper.getById(machineId.value());
        if (po == null) {
            return null;
        }
        return machineFactory.composeMachineFromMesPO(po);
    }

    @Override
    public List<Machine> filterMachineByOption(MachineOptions options, FactoryId factoryId) {
        var qo = new SearchMachineQO();
        qo.setInstituteId(PlanContext.getInstituteId());
        if (null != options) {
            qo.setTypeList(options.getMachineTypeList());
            qo.setBareSpandexTypeList(options.getBareSpandexList());
            if (CollectionUtils.isNotEmpty(options.getHighSpeedList())) {
                qo.setHighSpeed(options.getHighSpeedList().get(0));
            }
            if (CollectionUtils.isNotEmpty(options.getMachineSizeList())) {
                qo.setSizeList(options.getMachineSizeList().stream().map(it -> machineFactory.convertSizeToMachineSizeQO(it)).toList());
            }
            if (CollectionUtils.isNotEmpty(options.getFeatureList())) {
                qo.setAttrList(options.getFeatureList().stream().map(it -> new MachineAttrQO(it.getAttr().code(),
                        it.getValueList().stream().map(MachineAttrValue::value).toList())).toList());
            }
        }
        var machinePOList = mesMachineMapper.listByOptions(qo, null == factoryId ? null : factoryId.value());
        return composeMachineListFromMes(machinePOList);
    }

    @Override
    public PageData<Machine> pageQueryMachine(SearchMachine search) {
        IPage<MesMachinePO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchMachineQO();
        qo.setInstituteId(PlanContext.getInstituteId());
        qo.setFactoryIds(search.getFactoryIds());
        qo.setWorkshopIds(search.getWorkshopIds());
        qo.setGroupIds(search.getMachineGroupIds());
        qo.setStatusList(search.getStatusList());
        qo.setTypeList(search.getMachineTypeList());
        qo.setCylinderDiameterList(search.getCylinderDiameterList());
        qo.setNeedleNumberList(search.getNeedleNumberList());
        qo.setNeedleSpacingList(search.getNeedleSpacingList());
        qo.setBareSpandexTypeList(search.getBareSpandexList());
        qo.setAreaList(search.getAreaList());
        if (null != search.getHighSpeed()) {
            qo.setHighSpeed(search.getHighSpeed());
        }
        if (CollectionUtils.isNotEmpty(search.getFeatureList())) {
            qo.setAttrList(search.getFeatureList().stream().map(it -> new MachineAttrQO(it.getAttr().code(),
                    it.getValueList().stream().map(MachineAttrValue::value).toList())).toList());
        }
        IPage<MesMachinePO> pageRes = mesMachineMapper.searchMachine(page, qo);
        return PageData.fromPage(composeMachineListFromMes(pageRes.getRecords()), pageRes);
    }

    @Override
    public MachineOptions getMachineOptions() {
        try {
            return optionsCache.get(PlanContext.getInstituteId());
        } catch (ExecutionException e) {
            return MachineOptions.empty();
        }
    }

    @Override
    public void refreshOptions(InstituteId instituteId) {
        optionsCache.refresh(instituteId.value());
    }

    @Override
    public List<Machine> listAllMachine() {
        var poList = mesMachineMapper.listAll();
        return composeMachineListFromMes(poList);
    }

    @Override
    public List<Machine> listMachineByFactory(FactoryId factoryId) {
        var poList = machineMapper.listByFactoryId(factoryId.value());
        return composeMachineList(poList);
    }

    private MachineOptions loadMachineOptions(long instituteId) {
        var sizeList = mesMachineMapper.getAllSize();
        var typeList = mesMachineMapper.getAllMachineType();
        var areaList = mesMachineMapper.getAllArea();

        return new MachineOptions(
                sizeList.stream().map(it -> machineFactory.composeMachineSize(it)).filter(Objects::nonNull).toList(),
                typeList.stream().filter(StringUtils::isNotBlank).map(MachineType::new).toList(),
                Collections.emptyList(),
                Lists.newArrayList(HighSpeed.low(), HighSpeed.high()),
                Collections.emptyList(),
                areaList.stream().filter(StringUtils::isNotBlank).map(MachineArea::new).toList()
        );
    }

    private List<Machine> composeMachineList(List<MachinePO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        Set<Long> factorIds = Sets.newHashSet();
        Set<Long> workshopIds = Sets.newHashSet();
        Set<Long> machineGroupIds = Sets.newHashSet();
        for (var po : poList) {
            factorIds.add(po.getFactoryId());
            workshopIds.add(po.getWorkshopId());
            machineGroupIds.add(po.getMachineGroupId());
        }
        var factoryMap = organizationMapper.listById(factorIds.stream().toList())
                .stream()
                .map(it -> organizationFactory.composeFactoryFromPO(it))
                .collect(Collectors.toMap(it -> it.getId().value(), it -> it, (v1, v2) -> v1));
        var workshopMap = organizationMapper.listById(workshopIds.stream().toList())
                .stream()
                .map(it -> organizationFactory.composeWorkshopFromPO(it))
                .collect(Collectors.toMap(it -> it.getId().value(), it -> it, (v1, v2) -> v1));
        var machineGroupMap = organizationMapper.listById(machineGroupIds.stream().toList())
                .stream()
                .map(it -> organizationFactory.composeMachineGroupFromPO(it))
                .collect(Collectors.toMap(it -> it.getId().value(), it -> it, (v1, v2) -> v1));

        return poList.stream()
                .map(it -> machineFactory.composeMachineEntityFromPO(it, factoryMap.get(it.getFactoryId()),
                        workshopMap.get(it.getWorkshopId()),
                        machineGroupMap.get(it.getMachineGroupId())))
                .toList();
    }

    private List<Machine> composeMachineListFromMes(List<MesMachinePO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }

        return poList.stream()
                .map(it -> machineFactory.composeMachineFromMesPO(it))
                .toList();
    }
}
