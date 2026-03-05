package com.santoni.iot.aps.domain.plan;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;

import java.time.LocalDateTime;

public class TaskEntityBuilder {

    public static PlannedTask getPlannedTask() {
        return new PlannedTask(
                new TaskId(1),
                new TaskCode("task"),
                new FactoryId(1),
                new ProduceOrderCode("produce"),
                new WeavingPartOrderId(1),
                new MachineId(1),
                new StyleCode("code1"),
                new SkuCode("code"),
                new Part("身"),
                null,
                null,
                null,
                getProductionPlan(),
                ProduceQuantity.zero(),
                TaskStatus.INIT,
                null,
                null,
                null,
                null
        );
    }

    public static PlannedTask getPlannedTaskWithSegment() {
        return new PlannedTask(
                new TaskId(1),
                new TaskCode("task"),
                new FactoryId(1),
                new ProduceOrderCode("produce"),
                new WeavingPartOrderId(1),
                new MachineId(1),
                new StyleCode("code1"),
                new SkuCode("code"),
                new Part("身"),
                null,
                null,
                null,
                getProductionPlan(),
                ProduceQuantity.zero(),
                TaskStatus.INIT,
                null,
                null,
                Lists.newArrayList(getTaskSegment()),
                null
        );
    }

    public static ProductionPlan getProductionPlan() {
        LocalDateTime now = TimeUtil.getStartOf(LocalDateTime.now());
        return ProductionPlan.of(PlannedQuantity.of(100),
                PlanPeriod.of(now.minusDays(10), now.plusDays(10)));
    }

    public static TaskSegment getTaskSegment() {
        return new TaskSegment(
                new TaskSegmentId(1),
                new TaskId(1),
                getProductionPlan(),
                TaskStatus.INIT,
                null,
                SortIndex.init()
        );
    }
}
