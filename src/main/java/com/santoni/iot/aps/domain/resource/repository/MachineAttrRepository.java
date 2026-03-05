package com.santoni.iot.aps.domain.resource.repository;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttr;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrConfig;

import java.util.List;

public interface MachineAttrRepository {

    void createMachineAttrConfig(MachineAttrConfig machineAttrConfig);

    void updateMachineAttrConfig(MachineAttrConfig machineAttrConfig);

    MachineAttrConfig getConfigByAttr(MachineAttr attr);

    List<MachineAttrConfig> listConfigByAttr(List<MachineAttr> attrList);

    List<MachineAttrConfig> listAllConfig();

}
