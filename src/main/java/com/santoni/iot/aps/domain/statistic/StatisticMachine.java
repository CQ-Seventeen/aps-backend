package com.santoni.iot.aps.domain.statistic;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.statistic.constant.WorkLoadLevel;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

@Getter
public class StatisticMachine {

    private Quantity totalCount;

    private Map<MachineStatus, Quantity> statusCount;

    private Map<WorkLoadLevel, Quantity> workLoadCount;

    private WorkLoadSaturation saturation;

    private TimePeriod period;

    public StatisticMachine(TimePeriod period) {
        this.totalCount = Quantity.zero();
        this.statusCount = Maps.newHashMapWithExpectedSize(MachineStatus.values().length);
        this.workLoadCount = Maps.newHashMapWithExpectedSize(WorkLoadLevel.values().length);
        this.period = period;
        this.saturation = new WorkLoadSaturation(period.getSeconds());
    }

    public void countOneMachine(MachinePlan machinePlan) {
        totalCount.plusOne();
        if (statusCount.containsKey(machinePlan.getMachine().getStatus())) {
            statusCount.get(machinePlan.getMachine().getStatus()).plusOne();
        } else {
            statusCount.put(machinePlan.getMachine().getStatus(), Quantity.of(1));
        }
        countWorkLoad(machinePlan);
    }

    private void countWorkLoad(MachinePlan machinePlan) {
        var availableSeconds = machinePlan.getAvailableTime().getTotalSeconds();
        var level = determineWorkLoadLevel(availableSeconds);
        if (workLoadCount.containsKey(level)) {
            workLoadCount.get(level).plusOne();
        } else {
            workLoadCount.put(level, Quantity.of(1));
        }
        saturation.countOne(availableSeconds);
    }

    private WorkLoadLevel determineWorkLoadLevel(long availableSeconds) {
        var saturation = 1 - ((double) availableSeconds / this.saturation.getUnitSeconds());

        for (var workLoadLevel : WorkLoadLevel.values()) {
            if (saturation < workLoadLevel.getThreshold()) {
                return workLoadLevel;
            }
        }
        return WorkLoadLevel.LOW;
    }

    public int getLowLevelLoadCount() {
        return getLoadCount(WorkLoadLevel.LOW);
    }

    public int getMediumLevelLoadCount() {
        return getLoadCount(WorkLoadLevel.MEDIUM);
    }

    public int getHighLevelLoadCount() {
        return getLoadCount(WorkLoadLevel.HIGH);
    }

    private int getLoadCount(WorkLoadLevel level) {
        if (MapUtils.isEmpty(workLoadCount)) {
            return 0;
        }
        var count = workLoadCount.get(level);
        return null == count ? 0 : count.getValue();
    }
}
