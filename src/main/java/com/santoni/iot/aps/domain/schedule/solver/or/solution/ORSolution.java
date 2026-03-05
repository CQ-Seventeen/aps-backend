package com.santoni.iot.aps.domain.schedule.solver.or.solution;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.MachineResource;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.ToWeavedStyle;
import com.santoni.iot.aps.domain.schedule.solver.or.entity.WeavingTaskAssign;
import lombok.Data;

import java.util.List;

@PlanningSolution
@Data
public class ORSolution {

    @PlanningEntityCollectionProperty
    private List<WeavingTaskAssign> assignList;

    @ProblemFactCollectionProperty
    private List<ToWeavedStyle> styleList;

    @ProblemFactCollectionProperty
    private List<MachineResource> machineList;

    @PlanningScore
    private HardSoftScore score;

}
