package com.santoni.iot.aps.domain.execute.entity;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class MachineAggregateTable {

    private Map<CylinderDiameter, Map<NeedleSpacing, MachineAggregate>> table;

    public MachineAggregateTable() {
        this.table = Maps.newHashMap();
    }

    public void collectFromMachineList(List<Machine> machineList) {
        var machineMap = machineList.stream()
                .collect(Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter(),
                        Collectors.groupingBy(it -> it.getMachineSize().getNeedleSpacing())));
        for (var entry : machineMap.entrySet()) {
            for (var inner : entry.getValue().entrySet()) {
                var aggr = find(entry.getKey(), inner.getKey());
                if (null == aggr) {
                    table.computeIfAbsent(entry.getKey(), k -> Maps.newHashMap())
                            .put(inner.getKey(), MachineAggregate.initFrom(entry.getKey(), inner.getKey(), inner.getValue()));
                } else {
                    aggr.collectMachine(inner.getValue());
                }
            }
        }
    }

    public MachineAggregate find(CylinderDiameter diameter, NeedleSpacing needleSpacing) {
        return table.getOrDefault(diameter, Map.of()).get(needleSpacing);
    }

    public List<MachineAggregate> listAll() {
        return table.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .collect(Collectors.toList());
    }
}
