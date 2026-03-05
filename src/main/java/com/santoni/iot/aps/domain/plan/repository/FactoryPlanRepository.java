package com.santoni.iot.aps.domain.plan.repository;

import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTaskDetail;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;

import java.util.List;

public interface FactoryPlanRepository {

    TaskId saveTask(FactoryTask task);

    void saveTaskDetail(TaskId taskId, List<FactoryTaskDetail> details);

    List<FactoryTask> findByFactoryId(FactoryId factoryId, StartTime startTime, EndTime endTime);

    List<FactoryTask> listUnfinishedTaskByFactory(FactoryId factoryId);

    List<FactoryTask> listUnfinishedTask();

    List<FactoryTask> listByOrderIds(List<ProduceOrderId> produceOrderIds);

    List<FactoryTask> listByFactoryIds(List<FactoryId> factoryId, StartTime startTime, EndTime endTime);
}
