package com.santoni.iot.aps.infrastructure.repository;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.plan.constant.FactoryTaskStatus;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTaskDetail;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.plan.repository.FactoryPlanRepository;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.infrastructure.database.aps.FactoryPlannedTaskDetailMapper;
import com.santoni.iot.aps.infrastructure.database.aps.FactoryPlannedTaskMapper;
import com.santoni.iot.aps.infrastructure.factory.FactoryTaskFactory;
import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskDetailPO;
import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FactoryPlanRepositoryImpl implements FactoryPlanRepository {

    @Autowired
    private FactoryPlannedTaskMapper factoryPlannedTaskMapper;

    @Autowired
    private FactoryPlannedTaskDetailMapper factoryPlannedTaskDetailMapper;

    @Autowired
    private FactoryTaskFactory factoryTaskFactory;

    @Override
    public TaskId saveTask(FactoryTask task) {
        var po = factoryTaskFactory.convertPlannedTaskEntityToPO(task);
        factoryPlannedTaskMapper.insert(po);
        return new TaskId(po.getId());
    }

    @Override
    public void saveTaskDetail(TaskId taskId, List<FactoryTaskDetail> details) {
        var poList = details.stream().map(it -> factoryTaskFactory.convertTaskDetailEntityToPO(it, taskId)).toList();
        factoryPlannedTaskDetailMapper.batchInsert(poList);
    }

    @Override
    public List<FactoryTask> findByFactoryId(FactoryId factoryId, StartTime startTime, EndTime endTime) {
        var taskPOList = factoryPlannedTaskMapper.listByFactoryId(factoryId.value(), startTime.value(), endTime.value());
        return composeFactoryTaskList(taskPOList, true);
    }

    @Override
    public List<FactoryTask> listUnfinishedTaskByFactory(FactoryId factoryId) {
        var poList = factoryPlannedTaskMapper.listByFactoryAndStatus(factoryId.value(),
                Lists.newArrayList(FactoryTaskStatus.INIT.getCode(), FactoryTaskStatus.PRODUCING.getCode(), FactoryTaskStatus.SUSPEND.getCode()));
        return composeFactoryTaskList(poList, false);
    }

    @Override
    public List<FactoryTask> listUnfinishedTask() {
        var poList = factoryPlannedTaskMapper.listByStatus(PlanContext.getInstituteId(),
                Lists.newArrayList(FactoryTaskStatus.INIT.getCode(), FactoryTaskStatus.PRODUCING.getCode(), FactoryTaskStatus.SUSPEND.getCode()));
        return composeFactoryTaskList(poList, false);
    }

    @Override
    public List<FactoryTask> listByOrderIds(List<ProduceOrderId> produceOrderIds) {
        var taskPOList = factoryPlannedTaskMapper.listByOrderIdList(produceOrderIds.stream().map(ProduceOrderId::value).toList());
        return composeFactoryTaskList(taskPOList, false);
    }

    @Override
    public List<FactoryTask> listByFactoryIds(List<FactoryId> factoryIds, StartTime startTime, EndTime endTime) {
        var taskPOList = factoryPlannedTaskMapper.listByFactoryIdList(factoryIds.stream().map(FactoryId::value).toList(), startTime.value(), endTime.value());
        return composeFactoryTaskList(taskPOList, true);
    }

    private List<FactoryTask> composeFactoryTaskList(List<FactoryPlannedTaskPO> taskPOList, boolean needDetail) {
        if (CollectionUtils.isEmpty(taskPOList)) {
            return Collections.emptyList();
        }
        if (!needDetail) {
            return taskPOList.stream()
                    .map(it -> factoryTaskFactory.composePlannedTask(it, Collections.emptyList()))
                    .toList();
        }
        var detailMap = factoryPlannedTaskDetailMapper.listByTaskIds(taskPOList.stream().map(FactoryPlannedTaskPO::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(FactoryPlannedTaskDetailPO::getTaskId));
        return taskPOList.stream()
                .map(it -> factoryTaskFactory.composePlannedTask(it, detailMap.get(it.getId())))
                .toList();
    }
}
