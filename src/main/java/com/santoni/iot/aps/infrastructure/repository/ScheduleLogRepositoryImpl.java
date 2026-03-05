package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.SearchScheduleLog;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleLogRepository;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.infrastructure.database.aps.ScheduleLogMapper;
import com.santoni.iot.aps.infrastructure.factory.ScheduleFactory;
import com.santoni.iot.aps.infrastructure.po.ScheduleLogPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchScheduleLogQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Slf4j
@Repository
public class ScheduleLogRepositoryImpl implements ScheduleLogRepository {

    @Autowired
    private ScheduleLogMapper scheduleLogMapper;

    @Autowired
    private ScheduleFactory scheduleFactory;

    @Override
    public void logSchedule(ScheduleLog scheduleLog) {
        var po = scheduleFactory.convertToScheduleLogPO(scheduleLog);
        scheduleLogMapper.insert(po);
    }

    @Override
    public PageData<ScheduleLog> pageQueryScheduleLog(SearchScheduleLog search) {
        IPage<ScheduleLogPO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchScheduleLogQO();
        if (null != search.getType()) {
            qo.setType(search.getType().getCode());
        }
        var pageRes = scheduleLogMapper.searchScheduleLog(page, PlanContext.getInstituteId(), qo);
        if (CollectionUtils.isEmpty(pageRes.getRecords())) {
            return PageData.fromPage(Collections.emptyList(), pageRes);
        }
        var logList = pageRes.getRecords().stream().map(it -> scheduleFactory.composeScheduleLog(it)).toList();
        return PageData.fromPage(logList, pageRes);
    }
}
