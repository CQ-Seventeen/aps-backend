package com.santoni.iot.aps.application.schedule.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.schedule.ScheduleQueryApplication;
import com.santoni.iot.aps.application.schedule.assembler.ScheduleDTOAssembler;
import com.santoni.iot.aps.application.schedule.context.AssembleScheduleLogContext;
import com.santoni.iot.aps.application.schedule.dto.ScheduleLogDTO;
import com.santoni.iot.aps.application.schedule.query.PageScheduleLogQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.constant.ScheduleOperateType;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleLogRepository;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleTaskRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleQueryApplicationImpl implements ScheduleQueryApplication {

    @Autowired
    private ScheduleLogRepository scheduleLogRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private ScheduleTaskRepository scheduleTaskRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ScheduleDTOAssembler scheduleDTOAssembler;

    @Override
    public PageResult<ScheduleLogDTO> pageQueryScheduleLog(PageScheduleLogQuery query) {
        var search = new SearchScheduleLog(null == query.getType() ? null : ScheduleOperateType.getByCode(query.getType()), query);
        var pageData = scheduleLogRepository.pageQueryScheduleLog(search);
        if (CollectionUtils.isEmpty(pageData.getData())) {
            return PageResult.fromPageData(Collections.emptyList(), pageData);
        }
        var context = prepareAssembleScheduleLogContext(pageData.getData());
        var logDTOList = pageData.getData().stream().map(it -> scheduleDTOAssembler.assembleScheduleLogDTO(it, context)).toList();
        return PageResult.fromPageData(logDTOList, pageData);
    }

    private AssembleScheduleLogContext prepareAssembleScheduleLogContext(List<ScheduleLog> logList) {
        var taskPair = getTasks(logList);

        Set<MachineId> machineIdSet = Sets.newHashSet();
        Set<WeavingPartOrderId> orderIdSet = Sets.newHashSet();
        Map<Long, PlannedTask> plannedTaskMap = Maps.newHashMapWithExpectedSize(taskPair.getLeft().size());
        Map<Long, ScheduleTask> scheduleTaskMap = Maps.newHashMapWithExpectedSize(taskPair.getRight().size());
        for (var plannedTask : taskPair.getLeft()) {
            machineIdSet.add(plannedTask.getMachineId());
            orderIdSet.add(plannedTask.getWeavingPartOrderId());
            plannedTaskMap.putIfAbsent(plannedTask.getId().value(), plannedTask);
        }
        for (var scheduleTask : taskPair.getRight()) {
            machineIdSet.addAll(scheduleTask.getMachineIds());
            orderIdSet.addAll(scheduleTask.getStyleDemandList().stream().map(StylePartWeaveDemand::getWeavingPartOrderId).toList());
            scheduleTaskMap.putIfAbsent(scheduleTask.getTaskId().value(), scheduleTask);
        }
        Map<Long, WeavingPartOrder> orderIdMap = CollectionUtils.isEmpty(orderIdSet) ? Collections.emptyMap()
                : weavingOrderRepository.listWeavingPartOrderById(orderIdSet).stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it));
        Map<Long, Machine> machineMap = CollectionUtils.isEmpty(machineIdSet) ? Collections.emptyMap()
                : machineRepository.listMachineById(machineIdSet.stream().toList()).stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it));

        return new AssembleScheduleLogContext(orderIdMap, machineMap, plannedTaskMap, scheduleTaskMap);
    }

    private Pair<List<PlannedTask>, List<ScheduleTask>> getTasks(List<ScheduleLog> logList) {
        List<TaskId> planTaskIds = Lists.newArrayList();
        List<ScheduleTaskId> scheduleTaskIds = Lists.newArrayList();

        for (var log : logList) {
            if (ScheduleOperateType.STYLE_MACHINE.equals(log.getType())) {
                planTaskIds.add(log.getLogId().toTaskId());
            }
            if (ScheduleOperateType.MULTI_STYLE_MACHINE.equals(log.getType())) {
                scheduleTaskIds.add(log.getLogId().toScheduleTaskId());
            }
        }
        List<PlannedTask> taskList = Collections.emptyList();
        List<ScheduleTask> scheduleTaskList = Collections.emptyList();

        if (CollectionUtils.isNotEmpty(planTaskIds)) {
            taskList = machineTaskRepository.listByTaskIds(planTaskIds);
        }
        if (CollectionUtils.isNotEmpty(scheduleTaskIds)) {
            scheduleTaskList = scheduleTaskRepository.listByTaskIds(scheduleTaskIds);
        }
        return Pair.of(taskList, scheduleTaskList);
    }
}
