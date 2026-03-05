package com.santoni.iot.aps.application.schedule.context;

import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;

import java.util.Map;

public record AssembleScheduleLogContext(Map<Long, WeavingPartOrder> orderIdMap,
                                         Map<Long, Machine> machineMap,
                                         Map<Long, PlannedTask> plannedTaskMap,
                                         Map<Long, ScheduleTask> scheduleTaskMap) {}
