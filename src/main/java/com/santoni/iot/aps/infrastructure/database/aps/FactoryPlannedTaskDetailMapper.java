package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskDetailPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FactoryPlannedTaskDetailMapper {

    int insert(@Param("po") FactoryPlannedTaskDetailPO po);

    int batchInsert(@Param("poList") List<FactoryPlannedTaskDetailPO> poList);

    int update(@Param("po") FactoryPlannedTaskDetailPO po);

    List<FactoryPlannedTaskDetailPO> listByTaskId(@Param("taskId") long taskId);

    List<FactoryPlannedTaskDetailPO> listByTaskIds(@Param("taskIds") List<Long> taskIds);

}