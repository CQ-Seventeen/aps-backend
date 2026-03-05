package com.santoni.iot.aps.domain.schedule.entity.valueobj.context;

import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;

import java.util.List;

public record SubmitTaskContext(List<MachineId> machineIdList,
                                List<StylePartWeaveDemand> demandList,
                                TimePeriod timePeriod) {}
