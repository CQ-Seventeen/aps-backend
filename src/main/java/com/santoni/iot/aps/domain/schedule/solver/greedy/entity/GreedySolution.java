package com.santoni.iot.aps.domain.schedule.solver.greedy.entity;

import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.solver.or.entity.WeavingTaskAssign;
import lombok.Getter;

import java.util.List;

@Getter
public class GreedySolution {

    private List<ScheduleOnMachine> planList;

    public GreedySolution(List<ScheduleOnMachine> planList) {
        this.planList = planList;
    }
}
