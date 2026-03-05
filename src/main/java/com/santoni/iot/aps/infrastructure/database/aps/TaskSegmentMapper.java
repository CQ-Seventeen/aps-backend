package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.TaskSegmentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TaskSegmentMapper {

    long insert(@Param("po") TaskSegmentPO po);

    void batchInsert(@Param("poList") List<TaskSegmentPO> poList);

    void update(@Param("po") TaskSegmentPO po, @Param("operatorId") long operatorId);

    void batchUpdate(@Param("poList") List<TaskSegmentPO> poList, @Param("operatorId") long operatorId);

    void delete(@Param("id") long id, @Param("operatorId") long operatorId);

    void batchDelete(@Param("idList") Collection<Long> idList, @Param("operatorId") long operatorId);

    TaskSegmentPO getById(@Param("id") long id);

    List<TaskSegmentPO> listByTaskId(@Param("taskId") long taskId);

    List<TaskSegmentPO> listByTaskIdList(@Param("taskIdList") List<Long> taskIdList);

}
