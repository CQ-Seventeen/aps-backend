package com.santoni.iot.aps.domain.resource.repository;

import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineOptions;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineDeviceId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.SearchMachine;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;

import java.util.Collection;
import java.util.List;

public interface MachineRepository {

    void saveMachine(Machine machine);

    void batchSaveMachines(List<Machine> machines);

    List<Machine> listMachineById(Collection<MachineId> machineIdList);

    Machine findByDeviceId(FactoryId factoryId, MachineDeviceId deviceId);

    Machine detailById(MachineId machineId);

    List<Machine> filterMachineByOption(MachineOptions options, FactoryId factoryId);

    PageData<Machine> pageQueryMachine(SearchMachine search);

    MachineOptions getMachineOptions();

    void refreshOptions(InstituteId instituteId);

    List<Machine> listAllMachine();

    List<Machine> listMachineByFactory(FactoryId factoryId);
}
