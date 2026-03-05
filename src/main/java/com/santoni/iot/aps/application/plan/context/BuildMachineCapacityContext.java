package com.santoni.iot.aps.application.plan.context;

import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTaskDetail;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;

import java.util.List;
import java.util.Map;

public record BuildMachineCapacityContext(List<Factory> factoryList,
                                          Map<Integer, Map<Long, List<Machine>>> machineMap,
                                          List<Integer> cylinderList,
                                          Map<Long, Map<Integer, List<FactoryTaskDetail>>> taskMap) {
}
