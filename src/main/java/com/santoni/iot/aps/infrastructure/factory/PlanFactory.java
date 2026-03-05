package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PlanFactory {

    public ProductionPlan composeProductionPlan(int quantity,
                                                LocalDateTime start,
                                                LocalDateTime end) {
        return ProductionPlan.of(
                PlannedQuantity.of(quantity),
                PlanPeriod.of(start, end)
        );
    }
}
