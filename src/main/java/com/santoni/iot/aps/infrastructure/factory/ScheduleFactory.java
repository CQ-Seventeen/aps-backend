package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.constant.ScheduleOperateType;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.LogId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.OperateTime;
import com.santoni.iot.aps.infrastructure.po.ScheduleLogPO;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFactory {

    public ScheduleLogPO convertToScheduleLogPO(ScheduleLog log) {
        var po = new ScheduleLogPO();
        po.setInstituteId(PlanContext.getInstituteId());
        po.setLogId(log.getLogId().value());
        po.setType(log.getType().getCode());
        po.setOperateTime(log.getOperateTime().value());
        po.setCreatorId(PlanContext.getUserId());
        return po;
    }

    public ScheduleLog composeScheduleLog(ScheduleLogPO po) {
        return new ScheduleLog(new LogId(po.getLogId()),
                ScheduleOperateType.getByCode(po.getType()),
                null, null,
                new OperateTime(po.getOperateTime())
        );
    }
}
