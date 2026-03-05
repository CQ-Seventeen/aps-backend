package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

public class StyleRequirementTable {

    private TreeMap<LocalDateTime, EndTimeColumn> table = Maps.newTreeMap(LocalDateTime::compareTo);

    private List<StyleRequirement> allStyleRequirements = Lists.newArrayList();

    private List<LocalDateTime> allDeliveryTimeNodes = null;

    public void collectRequirements(LocalDateTime deliveryTime, List<StyleRequirement> requirements) {
        this.allStyleRequirements.addAll(requirements);
        table.put(deliveryTime, new EndTimeColumn(deliveryTime, requirements));
    }

    public List<LocalDateTime> getAllDeliveryTimes() {
        if (null == allDeliveryTimeNodes) {
            allDeliveryTimeNodes = Lists.newArrayList(table.keySet());
        }
        return allDeliveryTimeNodes;
    }

    public EndTimeColumn getCol(LocalDateTime deliveryTime) {
        return table.get(deliveryTime);
    }
}
