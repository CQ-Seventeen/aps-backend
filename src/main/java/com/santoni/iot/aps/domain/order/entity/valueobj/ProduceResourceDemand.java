package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineDays;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class ProduceResourceDemand {

    @Getter
    private ProduceOrder produceOrder;

    private Map<CylinderDiameter, MachineDays> demandMap;

    public ProduceResourceDemand(ProduceOrder produceOrder) {
        this.produceOrder = produceOrder;
        this.demandMap = Maps.newHashMap();
    }

    public void addSeconds(CylinderDiameter cylinderDiameter, long seconds) {
        var curDays = demandMap.get(cylinderDiameter);
        if (null == curDays) {
            demandMap.put(cylinderDiameter, MachineDays.fromSeconds(seconds));
        } else {
            curDays.addSeconds(seconds);
        }
    }

    public List<CylinderDiameter> getAllCylinders() {
        return demandMap.keySet().stream().toList();
    }

    public MachineDays getByCylinder(CylinderDiameter cylinder) {
        return demandMap.get(cylinder);
    }
}
