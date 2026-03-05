package com.santoni.iot.aps.domain.schedule.solver.or.entity;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.valuerange.CountableValueRange;
import ai.timefold.solver.core.api.domain.valuerange.ValueRange;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeFactory;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.MachineResource;
import com.santoni.iot.aps.domain.schedule.solver.or.fact.ToWeavedStyle;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PlanningEntity
@Data
public class WeavingTaskAssign {

    @PlanningId
    private String id;

    private ToWeavedStyle style;

    private MachineResource machineResource;

    @PlanningVariable(valueRangeProviderRefs = "pieceRange")
    private Integer piece = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeavingTaskAssign that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @ValueRangeProvider(id = "pieceRange")
    public CountableValueRange<Integer> getPieceRange() {
        int maxPiece = (int) (machineResource.getAvailableTime() / style.getTimePerPiece());
        int range = Math.min(maxPiece, style.getTotalQuantity());
        return ValueRangeFactory.createIntValueRange(0, range + 1);
    }
}
