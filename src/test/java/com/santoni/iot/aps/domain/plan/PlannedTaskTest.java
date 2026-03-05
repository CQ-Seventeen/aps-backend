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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PlannedTaskTest {

    @Test
    public void testCreateTask() {
        LocalDateTime now = TimeUtil.getStartOf(LocalDateTime.now());
        TaskSegment fakeSegment = new TaskSegment(
                new TaskSegmentId(1),
                new TaskId(2),
                ProductionPlan.of(PlannedQuantity.of(100),
                        PlanPeriod.of(now.minusDays(10), now.plusDays(10))),
                TaskStatus.INIT,
                null,
                SortIndex.init());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PlannedTask(
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
                        ProductionPlan.of(PlannedQuantity.of(100),
                                PlanPeriod.of(now.minusDays(10), now.plusDays(10))),
                        ProduceQuantity.zero(),
                        TaskStatus.INIT,
                        null,
                        null,
                        Lists.newArrayList(fakeSegment),
                        null
                ));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PlannedTask(
                        null,
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
                        ProductionPlan.of(PlannedQuantity.of(100),
                                PlanPeriod.of(now.minusDays(10), now.plusDays(10))),
                        ProduceQuantity.zero(),
                        TaskStatus.INIT,
                        null,
                        null,
                        Lists.newArrayList(fakeSegment),
                        null
                ));
    }

    @Test
    public void testInitSegment() {
        PlannedTask task = TaskEntityBuilder.getPlannedTask();
        task.initSegment();
        Assertions.assertFalse(task.getSegments().isEmpty());
    }

    @Test
    public void testSplitSegment() {
        PlannedTask task = TaskEntityBuilder.getPlannedTaskWithSegment();

        LocalDateTime now = TimeUtil.getStartOf(LocalDateTime.now());
        task.splitTask(null, PlannedQuantity.of(50), new PlanEndTime(now), new PlanStartTime(now.plusDays(3)));
        Assertions.assertEquals(2, task.getSegments().size());
    }

}
