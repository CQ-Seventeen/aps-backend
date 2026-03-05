package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SubmitTaskStatus;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleTaskRepository;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.infrastructure.database.aps.ScheduleTaskMapper;
import com.santoni.iot.aps.infrastructure.factory.ScheduleTaskFactory;
import com.santoni.iot.aps.infrastructure.po.ScheduleTaskPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchScheduleTaskQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ScheduleTaskRepositoryImpl implements ScheduleTaskRepository {

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;

    @Autowired
    private ScheduleTaskFactory scheduleTaskFactory;

    @Override
    public void createScheduleTask(ScheduleTask task) {
        var po = scheduleTaskFactory.convertToScheduleTaskPO(task);
        scheduleTaskMapper.insert(po);
    }

    @Override
    public void updateScheduleTask(ScheduleTask task) {
        var po = scheduleTaskFactory.convertToScheduleTaskPO(task);
        po.setId(task.getTaskId().value());
        scheduleTaskMapper.update(po, PlanContext.getUserId());
    }

    @Override
    public List<ScheduleTask> listUnprocessedTask() {
        var poList = scheduleTaskMapper.listByStatus(Lists.newArrayList(SubmitTaskStatus.SUBMITTED.getCode()));
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> scheduleTaskFactory.composeScheduleTask(it, false)).toList();
    }

    @Override
    public PageData<ScheduleTask> pageQueryScheduleTask(SearchScheduleTask search) {
        IPage<ScheduleTaskPO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchScheduleTaskQO();
        if (CollectionUtils.isNotEmpty(search.getStatus())) {
            qo.setStatusList(search.getStatus().stream().map(SubmitTaskStatus::getCode).toList());
        }
        var pageRes = scheduleTaskMapper.searchScheduleTask(page, PlanContext.getInstituteId(), qo);

        return PageData.fromPage(composeScheduleTaskListFromPOList(pageRes.getRecords()), pageRes);
    }

    @Override
    public ScheduleTask findScheduleTaskById(ScheduleTaskId taskId) {
        var po = scheduleTaskMapper.getById(taskId.value());
        if (null == po) {
            return null;
        }

        return scheduleTaskFactory.composeScheduleTask(po, true);
    }

    @Override
    public List<ScheduleTask> listByTaskIds(List<ScheduleTaskId> taskIds) {
        var poList = scheduleTaskMapper.listByTaskIdList(taskIds.stream().map(ScheduleTaskId::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> scheduleTaskFactory.composeScheduleTask(it, false)).toList();
    }

    private List<ScheduleTask> composeScheduleTaskListFromPOList(List<ScheduleTaskPO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> scheduleTaskFactory.composeScheduleTask(it, false)).toList();
    }
}
