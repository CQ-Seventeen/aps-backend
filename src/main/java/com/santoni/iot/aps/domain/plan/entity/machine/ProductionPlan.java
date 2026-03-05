package com.santoni.iot.aps.domain.plan.entity.machine;

import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanStartTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import lombok.Getter;

import java.time.Duration;

@Getter
public class ProductionPlan {

    private PlanPeriod period;

    private PlannedQuantity quantity;

    private ProductionPlan(PlanPeriod period, PlannedQuantity quantity) {
        this.period = period;
        this.quantity = quantity;
    }

    public void suspend(PlanEndTime planEndTime) {
        this.period = PlanPeriod.of(period.getStart(), planEndTime);
    }

    public static ProductionPlan of(PlannedQuantity quantity, PlanPeriod period) {
        if (null == quantity) {
            throw new IllegalArgumentException("生产计划需指定计划数量");
        }
        if (null == period) {
            throw new IllegalArgumentException("生产计划需指定计划时间段");
        }
        return new ProductionPlan(period, quantity);
    }

    public ProductionPlan modifyAndGenerateNextPlan(PlannedQuantity remainQuantity, PlanEndTime curPlanEndTime, PlanStartTime nextPlanStartTime) {
        if (remainQuantity.biggerThan(this.quantity)) {
            throw new IllegalArgumentException("计划无法切分成待生产数量更多的子计划");
        }
        if (curPlanEndTime.value().isBefore(period.getStart().value())) {
            throw new IllegalArgumentException("计划结束时间不应早于开始时间");
        }
        long remainSeconds = Duration.between(period.getStart().value(), curPlanEndTime.value()).getSeconds();
        if (!canFinish(remainQuantity, remainSeconds)) {
            throw new IllegalArgumentException("预留时间与预留件数不匹配，无法满足生产需求");
        }
        PlannedQuantity nextQuantity = this.quantity.minus(remainQuantity);
        long nextSeconds = period.totalSeconds() - remainSeconds;

        modifySelf(curPlanEndTime, remainQuantity);

        return of(nextQuantity, PlanPeriod.byStartTime(nextPlanStartTime, nextSeconds));
    }

    public void postponeEndTime(long seconds) {
        this.period = PlanPeriod.of(this.period.getStart(), new PlanEndTime(
                this.period.getEnd().value().plusSeconds(seconds)
        ));
    }

    public void postpone(long seconds) {
        this.period = PlanPeriod.of(this.period.getStart().value().plusSeconds(seconds),
                this.period.getEnd().value().plusSeconds(seconds)
        );
    }

    public int compareByStart(ProductionPlan other) {
        return this.period.getStart().value().compareTo(other.getPeriod().getStart().value());
    }

    public int compareByEnd(ProductionPlan other) {
        return this.period.getEnd().value().compareTo(other.getPeriod().getEnd().value());
    }

    public boolean startEarlierThanStart(ProductionPlan other) {
        return !this.period.getStart().value().isAfter(other.getPeriod().getStart().value());
    }

    public boolean endEarlierThanStart(ProductionPlan other) {
        return !this.period.getEnd().value().isAfter(other.getPeriod().getStart().value());
    }

    public boolean startLaterThanEnd(ProductionPlan other) {
        return this.period.getStart().value().isAfter(other.getPeriod().getEnd().value());
    }

    private void modifySelf(PlanEndTime planEndTime, PlannedQuantity remainQuantity) {
        this.period = PlanPeriod.of(period.getStart(), planEndTime);
        this.quantity = remainQuantity;
    }

    private boolean canFinish(PlannedQuantity remainQuantity, long remainSeconds) {
        double perPieceSeconds = (double) remainSeconds / remainQuantity.getValue();
        return deviationAllowed(perPieceSeconds, secondsPerPiece());
    }

    private double secondsPerPiece() {
        return (double) period.totalSeconds() / quantity.getValue();
    }

    private boolean deviationAllowed(double curPerPieceTime, double planPerPieceTime) {
        return Math.abs(curPerPieceTime - planPerPieceTime) < PlanConstant.ALLOW_PER_PRODUCT_DEVIATION;
    }
}
