package com.santoni.iot.aps.domain.plan.entity.machine;

import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.valueobj.*;
import com.santoni.iot.aps.domain.execute.entity.Execution;
import lombok.Getter;

@Getter
public class TaskSegment {

    private TaskSegmentId id;

    private TaskId taskId;

    private ProductionPlan plan;

    private TaskStatus status;

    private Execution execution;

    private SortIndex index;

    public TaskSegment split(PlannedQuantity remainQuantity, PlanEndTime curPlanEndTime, PlanStartTime nextPlanStartTime) {
        ProductionPlan nextNewPlan = plan.modifyAndGenerateNextPlan(remainQuantity, curPlanEndTime, nextPlanStartTime);

        return newSegment(this.taskId, nextNewPlan, TaskStatus.INIT, this.index.next());
    }

    public void reAssignAfterSplit(long postponeSeconds) {
        this.plan.postpone(postponeSeconds);
        this.index.backward();
    }

    public static TaskSegment newSegment(TaskId taskId,
                                         ProductionPlan plan,
                                         TaskStatus status,
                                         SortIndex index) {
        return new TaskSegment(null, taskId, plan, status, null, index);
    }

    public TaskSegment(TaskSegmentId id,
                       TaskId taskId,
                       ProductionPlan plan,
                       TaskStatus status,
                       Execution execution,
                       SortIndex index) {
        this.id = id;
        this.taskId = taskId;
        this.plan = plan;
        this.status = status;
        this.execution = execution;
        this.index = index;
    }
}
