package com.santoni.iot.aps.application.plan;

import com.santoni.iot.aps.application.plan.command.*;

public interface PlanOperateApplication {

    void manuallyAssignTask(ManuallyAssignTaskCommand command);

    void updateTask(UpdateTaskCommand command);

    void splitTask(SplitTaskCommand command);

    void freeUpTime(FreeUpTimeCommand command);

    void batchAssignTask(BatchAssignTaskCommand command);

    void realtimeAdjustTask();

    void cancelTask(CancelTaskCommand command);

    void manualSyncTask(long taskId);

}