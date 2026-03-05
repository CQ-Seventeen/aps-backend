package com.santoni.iot.aps.domain.schedule.solver.or.constraint;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import com.santoni.iot.aps.domain.schedule.solver.or.entity.WeavingTaskAssign;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.MachineResource;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.ToWeavedStyle;

public class ConstraintProvider implements ai.timefold.solver.core.api.score.stream.ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                styleQuantityConstraint(constraintFactory),
                machineTimeConstraint(constraintFactory),
                minimizeMachineStylesReward(constraintFactory),
                machineStylesPenalize(constraintFactory),
                styleQuantityReward(constraintFactory)
        };
    }

    private Constraint styleQuantityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ToWeavedStyle.class)
                .join(WeavingTaskAssign.class, Joiners.equal(ToWeavedStyle::getStyleCode, assign -> assign.getStyle().getStyleCode()))
                .groupBy((style, assign) -> style,
                        ConstraintCollectors.sum((style, assign) -> assign.getPiece()))
                .filter((style, totalPiece) -> style.getTotalQuantity() > totalPiece)
                .penalize(HardSoftScore.of(1000, 0))
                .asConstraint("Style Quantity Not Finish");
    }

    private Constraint styleQuantityReward(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ToWeavedStyle.class)
                .join(WeavingTaskAssign.class, Joiners.equal(ToWeavedStyle::getStyleCode, assign -> assign.getStyle().getStyleCode()))
                .groupBy((style, assign) -> style,
                        ConstraintCollectors.sum((style, assign) -> assign.getPiece()))
                .filter((style, totalPiece) -> style.getTotalQuantity() == totalPiece)
                .reward(HardSoftScore.of(1000, 0))
                .asConstraint("Style Quantity Finish");
    }

    private Constraint machineTimeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(MachineResource.class)
                .join(WeavingTaskAssign.class)
                .filter((machine, assign) -> assign.getStyle() != null) // 确保 style 不为 null
                .groupBy((machine, assign) -> machine,
                        ConstraintCollectors.sumLong((machine, assign) ->
                                assign.getPiece() * (long) assign.getStyle().getTimePerPiece()))
                .filter((machine, totalTime) -> machine.getAvailableTime() < totalTime)
                .penalize(HardSoftScore.of(100, 0))
                .asConstraint("Machine Time Constraint");
    }


    private Constraint minimizeMachineStylesReward(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(WeavingTaskAssign.class)
                .filter(weavingTaskAssign -> weavingTaskAssign.getPiece() > 0)
                .groupBy(WeavingTaskAssign::getMachineResource,
                        ConstraintCollectors.toSet(WeavingTaskAssign::getStyle))
                .reward(HardSoftScore.ONE_SOFT, // 奖励 1soft 当样式数量为 1
                        (machineResource, styleSet) -> styleSet.size() == 1 ? 1 : 0)
                .asConstraint("reward machine's styles");
    }

    private Constraint machineStylesPenalize(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(WeavingTaskAssign.class)
                .filter(weavingTaskAssign -> weavingTaskAssign.getPiece() > 0)
                .groupBy(WeavingTaskAssign::getMachineResource,
                        ConstraintCollectors.toSet(WeavingTaskAssign::getStyle))
                .penalize(HardSoftScore.ONE_SOFT, // 惩罚样式数量多于 1
                        (machineResource, styleSet) -> Math.max(0, styleSet.size() - 1))
                .asConstraint("penalize machine's styles");
    }


}
