package com.santoni.iot.aps.infrastructure.repository;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttr;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrConfig;
import com.santoni.iot.aps.domain.resource.repository.MachineAttrRepository;
import com.santoni.iot.aps.infrastructure.database.aps.MachineAttrConfigMapper;
import com.santoni.iot.aps.infrastructure.factory.MachineFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MachineAttrRepositoryImpl implements MachineAttrRepository {

    @Autowired
    private MachineAttrConfigMapper machineAttrConfigMapper;

    @Autowired
    private MachineFactory machineFactory;

    @Override
    public void createMachineAttrConfig(MachineAttrConfig machineAttrConfig) {
        var po = machineFactory.convertMachineAttrConfigToPO(machineAttrConfig);
        var userId = PlanContext.getUserId();
        po.setCreatorId(userId);
        po.setOperatorId(userId);
        machineAttrConfigMapper.insert(po);
    }

    @Override
    public void updateMachineAttrConfig(MachineAttrConfig machineAttrConfig) {
        var po = machineFactory.convertMachineAttrConfigToPO(machineAttrConfig);
        machineAttrConfigMapper.update(po, PlanContext.getUserId());
    }

    @Override
    public MachineAttrConfig getConfigByAttr(MachineAttr attr) {
        var po = machineAttrConfigMapper.getByAttrCode(PlanContext.getInstituteId(), attr.code());
        if (null == po) {
            return null;
        }
        return machineFactory.composeMachineAttrConfig(po);
    }

    @Override
    public List<MachineAttrConfig> listConfigByAttr(List<MachineAttr> attrList) {
        var poList = machineAttrConfigMapper.listByCodeList(PlanContext.getInstituteId(), attrList.stream().map(MachineAttr::code).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> machineFactory.composeMachineAttrConfig(it)).toList();
    }

    @Override
    public List<MachineAttrConfig> listAllConfig() {
        var poList = machineAttrConfigMapper.listAll(PlanContext.getInstituteId());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> machineFactory.composeMachineAttrConfig(it)).toList();
    }
}
