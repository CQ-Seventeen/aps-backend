package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot;

import com.santoni.iot.aps.common.utils.TriMap;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.EndTimeColumn;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.StyleRequirement;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArrangeMidResult {

    private LocalDateTime endTimeNode;

    private List<StyleRequirement> requireList;

    private boolean satisfied;

    private TriMap<Integer, Integer, Boolean, OccupiedTimeByNode> occupiedTimePeriod = new TriMap<>();

    private TriMap<Integer, Integer, Boolean, Double> leftTimeRequire = new TriMap<>();

    public ArrangeMidResult(EndTimeColumn endTimeCol) {
        this.endTimeNode = endTimeCol.getEndTime();
        this.requireList = endTimeCol.getRequirementList();
    }

    public void occupyTimePeriod(Integer cylinderDiameter,
                                 Integer needleSpacing,
                                 Boolean bareSpandex,
                                 OccupiedTimeByNode timePeriod) {

        occupiedTimePeriod.put(cylinderDiameter, needleSpacing, bareSpandex, timePeriod);
    }

    public void recordLeftTime(Integer cylinderDiameter,
                               Integer needleSpacing,
                               Boolean bareSpandex,
                               Double leftTime) {
        leftTimeRequire.put(cylinderDiameter, needleSpacing, bareSpandex, leftTime);
    }

    public void satisfied() {
        this.satisfied = true;
    }

    public void notSatisfied() {
        this.satisfied = false;
    }
}
