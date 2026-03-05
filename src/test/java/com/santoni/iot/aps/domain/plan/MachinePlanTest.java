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
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.*;
import com.santoni.iot.aps.domain.resource.MachineEntityBuilder;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class MachinePlanTest {

    @Test
    public void testExpandTask() {
        var machinePlan = MachinePlan.of(MachineEntityBuilder.getMachine(),
                buildPlannedTasks());
        var segments = machinePlan.expandPlannedTasks();
        Assertions.assertEquals(4, segments.size());
        Assertions.assertEquals(1, segments.get(0).getId().value());
        Assertions.assertEquals(4, segments.get(segments.size() - 1).getId().value());
    }


    private List<PlannedTask> buildPlannedTasks() {
        LocalDateTime now = TimeUtil.getStartOf(LocalDateTime.now());

        TaskSegment segment1 = new TaskSegment(
                new TaskSegmentId(1),
                new TaskId(1),
                ProductionPlan.of(PlannedQuantity.of(50),
                        PlanPeriod.of(now, now.plusDays(10))),
                TaskStatus.INIT,
                null,
                SortIndex.of(1)
        );

        TaskSegment segment2 = new TaskSegment(
                new TaskSegmentId(2),
                new TaskId(1),
                ProductionPlan.of(PlannedQuantity.of(50),
                        PlanPeriod.of(now.plusDays(10), now.plusDays(20))),
                TaskStatus.INIT,
                null,
                SortIndex.of(2)
        );

        PlannedTask task1 = new PlannedTask(
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
                        PlanPeriod.of(now, now.plusDays(20))),
                ProduceQuantity.zero(),
                TaskStatus.INIT,
                null,
                null,
                Lists.newArrayList(segment1, segment2),
                null
        );

        TaskSegment segment3 = new TaskSegment(
                new TaskSegmentId(3),
                new TaskId(2),
                ProductionPlan.of(PlannedQuantity.of(50),
                        PlanPeriod.of(now.plusDays(20), now.plusDays(30))),
                TaskStatus.INIT,
                null,
                SortIndex.of(1)
        );

        TaskSegment segment4 = new TaskSegment(
                new TaskSegmentId(4),
                new TaskId(2),
                ProductionPlan.of(PlannedQuantity.of(50),
                        PlanPeriod.of(now.plusDays(30), now.plusDays(40))),
                TaskStatus.INIT,
                null,
                SortIndex.of(2)
        );

        PlannedTask task2 = new PlannedTask(
                new TaskId(2),
                new TaskCode("task"),
                new FactoryId(1),
                new ProduceOrderCode("produce"),
                new WeavingPartOrderId(2),
                new MachineId(2),
                new StyleCode("code1"),
                new SkuCode("code"),
                new Part("身"),
                null,
                null,
                null,
                ProductionPlan.of(PlannedQuantity.of(100),
                        PlanPeriod.of(now.plusDays(20), now.plusDays(40))),
                ProduceQuantity.zero(),
                TaskStatus.INIT,
                null,
                null,
                Lists.newArrayList(segment3, segment4),
                null
        );
        return Lists.newArrayList(task1, task2);
    }
}
