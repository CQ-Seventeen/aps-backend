package com.santoni.iot.aps.domain.schedule.entity.valueobj.context;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;

import java.util.List;
import java.util.Map;

public record ScheduleContext(ScheduleTask task,
                              List<StylePartWeaveDemand> demandList,
                              List<MachinePlan> machinePlanList,
                              Map<Long, List<StyleComponent>> machineStylePair,
                              Map<String, Map<String, Map<String, StyleComponent>>> styleComponentMap,
                              TimePeriod timePeriod) {}
