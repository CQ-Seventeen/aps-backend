package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.SearchPlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.infrastructure.database.aps.PlannedTaskMapper;
import com.santoni.iot.aps.infrastructure.database.aps.TaskSegmentMapper;
import com.santoni.iot.aps.infrastructure.factory.PlannedTaskFactory;
import com.santoni.iot.aps.infrastructure.po.PlannedTaskPO;
import com.santoni.iot.aps.infrastructure.po.TaskSegmentPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchPlannedTaskQO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MachineTaskRepositoryImpl implements MachineTaskRepository {

    @Autowired
    private PlannedTaskMapper plannedTaskMapper;

    @Autowired
    private TaskSegmentMapper taskSegmentMapper;

    @Autowired
    private PlannedTaskFactory plannedTaskFactory;

    @Override
    public TaskId createTask(PlannedTask task) {
        var po = plannedTaskFactory.convertPlannedTaskEntityToPO(task);
        po.setCreatorId(PlanContext.getUserId());
        po.setOperatorId(PlanContext.getUserId());
        plannedTaskMapper.insert(po);
        return new TaskId(po.getId());
    }

    @Override
    public void createTaskSegment(TaskSegment segment) {
        var po = plannedTaskFactory.convertTaskSegmentEntityToPO(segment);
        insertTaskSegment(po, PlanContext.getUserId());
    }

    @Override
    public void saveTask(PlannedTask task, boolean saveSegment) {
        var taskPO = plannedTaskFactory.convertPlannedTaskEntityToPO(task);
        long userId = PlanContext.getUserId();
        taskPO.setId(task.getId().value());
        plannedTaskMapper.update(taskPO, userId);
        if (saveSegment && CollectionUtils.isNotEmpty(task.getSegments())) {
            saveTaskSegment(task.getSegments(), task.getId().value(), userId);
        }
    }

    @Override
    public List<PlannedTask> findByMachine(MachineId machineId, StartTime startTime, PlanEndTime endTime) {
        var taskList = plannedTaskMapper.listByMachineId(machineId.value(),
                null == startTime ? null : startTime.value(),
                null == endTime ? null : endTime.value());
        return composePlannedTaskList(taskList);
    }

    @Override
    public List<PlannedTask> listByMachines(Collection<MachineId> machineIds, StartTime startTime, PlanEndTime endTime) {
        var taskList = plannedTaskMapper.listByMachineIdList(machineIds.stream().map(MachineId::value).toList(),
                null == startTime ? null : startTime.value(),
                null == endTime ? null : endTime.value());
        return composePlannedTaskList(taskList);
    }

    @Override
    public List<PlannedTask> listByWeavingOrder(WeavingOrderId weavingOrderId) {
        var taskList = plannedTaskMapper.listByWeavingPartOrderId(weavingOrderId.value());
        return composePlannedTaskList(taskList);
    }

    @Override
    public List<PlannedTask> listByTaskIds(List<TaskId> taskIds) {
        var taskList = plannedTaskMapper.listByIds(taskIds.stream().map(TaskId::value).toList());
        return composePlannedTaskList(taskList);
    }

    @Override
    public PageData<PlannedTask> pageQueryTask(SearchPlannedTask search) {
        IPage<PlannedTaskPO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchPlannedTaskQO();
        qo.setInstituteId(PlanContext.getInstituteId());
        if (null != search.getFactoryId()) {
            qo.setFactoryId(search.getFactoryId().value());
        }
        if (null != search.getProduceOrderCode()) {
            qo.setProduceOrderCode(search.getProduceOrderCode().value());
        }
        if (null != search.getStyleCode()) {
            qo.setStyleCode(search.getStyleCode().value());
        }
        if (null != search.getStartTime()) {
            qo.setStartTime(search.getStartTime().value());
        }
        if (null != search.getEndTime()) {
            qo.setEndTime(search.getEndTime().value());
        }
        var pageRes = plannedTaskMapper.searchTask(page, qo);
        return PageData.fromPage(composePlannedTaskList(pageRes.getRecords()), pageRes);
    }

    @Override
    public List<PlannedTask> listByEndTime(List<MachineId> machineIds, EndTime endTimeStart, EndTime endTimeEnd) {
        var taskList = plannedTaskMapper.listByMachineAndEndTime(machineIds.stream().map(it -> it.value()).toList(),
                endTimeStart.value(), endTimeEnd.value());
        return composePlannedTaskList(taskList);
    }

    @Override
    public List<PlannedTask> listAllProcessingTask(FactoryId factoryId) {
        List<PlannedTaskPO> taskList;
        if (null == factoryId) {
            taskList = plannedTaskMapper.listByStatus(Lists.newArrayList(TaskStatus.INIT.getCode(), TaskStatus.PRODUCING.getCode(), TaskStatus.SUSPEND.getCode()));
        } else {
            taskList = plannedTaskMapper.listByFactoryIdAndStatus(factoryId.value(),
                    Lists.newArrayList(TaskStatus.INIT.getCode(), TaskStatus.PRODUCING.getCode(), TaskStatus.SUSPEND.getCode()));
        }
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        return taskList.stream()
                .map(it -> plannedTaskFactory.composePlannedTask(it, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    public PlannedTask taskDetailById(TaskId taskId) {
        var po = plannedTaskMapper.getById(taskId.value());
        if (null == po) {
            return null;
        }
        return composePlannedTask(po);
    }

    @Override
    public List<PlannedTask> listByProduceOrder(Collection<ProduceOrderCode> produceOrderCodes) {
        var codeStrList = produceOrderCodes.stream().map(ProduceOrderCode::value).toList();
        var taskList = plannedTaskMapper.listByProduceOrderCode(codeStrList);
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        return taskList.stream()
                .map(it -> plannedTaskFactory.composePlannedTask(it, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlannedTask> listByTime(StartTime startTime, EndTime endTime) {
        var taskList = plannedTaskMapper.listByTime(startTime.value(), endTime.value());
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        return taskList.stream()
                .map(it -> plannedTaskFactory.composePlannedTask(it, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    public PlannedTask find(MachineId machineId, ProduceOrderCode orderCode, StyleCode styleCode, Size size, Part part) {
        var po = plannedTaskMapper.find(machineId.value(), orderCode.value(), styleCode.value(), size.value(), part.value());
        if (null == po) {
            return null;
        }
        return plannedTaskFactory.composePlannedTask(po, Collections.emptyList());
    }

    private PlannedTask composePlannedTask(PlannedTaskPO po) {
        var segmentList = taskSegmentMapper.listByTaskId(po.getId());
        return plannedTaskFactory.composePlannedTask(po, segmentList);
    }

    private List<PlannedTask> composePlannedTaskList(List<PlannedTaskPO> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return List.of();
        }
        List<Long> taskIds = taskList.stream().map(PlannedTaskPO::getId).toList();
        var segmentMap = taskSegmentMapper.listByTaskIdList(taskIds).stream().collect(Collectors.groupingBy(TaskSegmentPO::getTaskId));
        return taskList.stream()
                .map(it -> plannedTaskFactory.composePlannedTask(it, segmentMap.get(it.getId())))
                .collect(Collectors.toList());
    }

    private void saveTaskSegment(List<TaskSegment> segmentList, long taskId, long userId) {
        Set<Long> existIds = taskSegmentMapper.listByTaskId(taskId).stream().map(TaskSegmentPO::getId).collect(Collectors.toSet());

        List<TaskSegmentPO> toInsertList = Lists.newArrayList();
        List<TaskSegmentPO> toUpdateList = Lists.newArrayList();

        for (TaskSegment segment : segmentList) {
            var po = plannedTaskFactory.convertTaskSegmentEntityToPO(segment);
            if (null == segment.getId()) {
                po.setCreatorId(userId);
                po.setOperatorId(userId);
                toInsertList.add(po);
            } else {
                po.setId(segment.getId().value());
                toUpdateList.add(po);
                existIds.remove(po.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(toInsertList)) {
            taskSegmentMapper.batchInsert(toInsertList);
        }
        if (CollectionUtils.isNotEmpty(toUpdateList)) {
            taskSegmentMapper.batchUpdate(toUpdateList, userId);
        }
        if (CollectionUtils.isNotEmpty(existIds)) {
            taskSegmentMapper.batchDelete(existIds, userId);
        }
    }

    private void insertTaskSegment(TaskSegmentPO po, long userId) {
        po.setCreatorId(userId);
        po.setOperatorId(userId);
        taskSegmentMapper.insert(po);
    }
}
