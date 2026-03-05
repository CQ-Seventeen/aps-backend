package com.santoni.iot.aps.domain.order;

import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.plan.PlanEntityBuilder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WeavingOrderTest {

    @Test
    public void testPlannedToMachines() {
        WeavingOrder weavingOrder = OrderEntityBuilder.getWeavingOrder();
        MachinePlan machine = PlanEntityBuilder.getMachinePlan();

        LocalDateTime now = TimeUtil.getStartOf(LocalDateTime.now());
        ProductionPlan plan = ProductionPlan.of(PlannedQuantity.of(100),
                PlanPeriod.of(now.plusDays(10), now.plusDays(20)));
        // todo
//        PlannedTask task = weavingPartOrder.arrangeToMachine(new Part("大身"), machine, plan);
//        Assertions.assertNotNull(task);
    }

}
