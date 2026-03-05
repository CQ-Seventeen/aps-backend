package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;

import java.time.Duration;

@Getter
public class ProduceEstimation {

    private StyleComponent styleComponent;

    private Machine machine;

    private PlanPeriod planPeriod;

    private StartTime startTime;

    private EndTime endTime;

    private Quantity quantity;

    public void estimateTimeByStart(StartTime start, Quantity quantity) {
        if (start.value().isAfter(planPeriod.getEnd().value())) {
            throw new IllegalArgumentException("开始时间不得晚于可计划终止时间");
        }
        var planTimePeriod = planPeriod.toTimePeriod();
        if (start.value().isBefore(planTimePeriod.getStart().value())) {
            this.startTime = planTimePeriod.getStart();
        } else {
            this.startTime = start;
        }
        var needSeconds = (long) Math.ceil(styleComponent.getExpectedProduceTime().value() * quantity.getValue() / styleComponent.getEfficiency().doubleValue());
        var availableSeconds = Duration.between(this.startTime.value(), planTimePeriod.getEnd().value()).toSeconds();
        if (needSeconds <= availableSeconds) {
            this.quantity = quantity;
            this.endTime = new EndTime(this.startTime.value().plusSeconds(needSeconds));
        } else {
            this.endTime = planTimePeriod.getEnd();
            this.quantity = calculateQuantity(availableSeconds);
        }
    }

    public void estimateTimeByEnd(EndTime end, Quantity quantity) {
        if (end.value().isBefore(planPeriod.getStart().value())) {
            throw new IllegalArgumentException("结束时间不得早于可计划起始时间");
        }
        var planTimePeriod = planPeriod.toTimePeriod();
        if (end.value().isAfter(planTimePeriod.getEnd().value())) {
            this.endTime = planTimePeriod.getEnd();
        } else {
            this.endTime = end;
        }
        var needSeconds = (long) Math.ceil(styleComponent.getExpectedProduceTime().value() * quantity.getValue() / styleComponent.getEfficiency().doubleValue());
        var availableSeconds = Duration.between(planTimePeriod.getStart().value(), this.endTime.value()).toSeconds();
        if (needSeconds <= availableSeconds) {
            this.quantity = quantity;
            this.startTime = new StartTime(this.endTime.value().minusSeconds(needSeconds));
        } else {
            this.startTime = planTimePeriod.getStart();
            this.quantity = calculateQuantity(availableSeconds);
        }
    }

    public void estimateMaxQuantity(Quantity quantity) {
        var availableSeconds = planPeriod.totalSeconds();
        var needSeconds = (long) Math.ceil(styleComponent.getExpectedProduceTime().value() * quantity.getValue() / styleComponent.getEfficiency().doubleValue());
        if (needSeconds <= availableSeconds) {
            this.quantity = quantity;
        } else {
            this.quantity = calculateQuantity(availableSeconds);
        }
    }

    private Quantity calculateQuantity(long availableSeconds) {
        return Quantity.of((int) Math.floor((availableSeconds * styleComponent.getEfficiency().doubleValue()) / styleComponent.getExpectedProduceTime().value()));
    }

    private ProduceEstimation(StyleComponent styleComponent,
                              Machine machine,
                              PlanPeriod planPeriod,
                              StartTime startTime,
                              EndTime endTime,
                              Quantity quantity) {
        this.styleComponent = styleComponent;
        this.machine = machine;
        this.planPeriod = planPeriod;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
    }

    public static ProduceEstimation basicOf(StyleComponent styleComponent, Machine machine, PlanPeriod planPeriod) {
        return new ProduceEstimation(styleComponent, machine, planPeriod, null, null, null);
    }
}
