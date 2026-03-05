package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.ScheduleLogPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchScheduleLogQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScheduleLogMapper {

    long insert(@Param("po") ScheduleLogPO po);

    IPage<ScheduleLogPO> searchScheduleLog(IPage<ScheduleLogPO> page, @Param("instituteId") long instituteId,
                                           @Param("qo")SearchScheduleLogQO qo);


}
