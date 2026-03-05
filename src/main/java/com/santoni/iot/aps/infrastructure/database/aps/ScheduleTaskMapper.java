package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.ScheduleTaskPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchScheduleTaskQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleTaskMapper {

    long insert(@Param("po") ScheduleTaskPO po);

    void update(@Param("po") ScheduleTaskPO po, @Param("operatorId") long operatorId);

    void delete(@Param("id") long id, @Param("operatorId") long operatorId);

    ScheduleTaskPO getById(@Param("id") long id);

    List<ScheduleTaskPO> listByTaskIdList(@Param("taskIdList") List<Long> taskIdList);

    List<ScheduleTaskPO> listByStatus(@Param("statusList") List<Integer> statusList);

    IPage<ScheduleTaskPO> searchScheduleTask(IPage<ScheduleTaskPO> page, @Param("instituteId") long instituteId, @Param("qo") SearchScheduleTaskQO qo);

}
