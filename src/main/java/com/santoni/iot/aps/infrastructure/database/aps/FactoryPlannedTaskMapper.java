package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FactoryPlannedTaskMapper {

    int insert(@Param("po") FactoryPlannedTaskPO po);

    void batchInsert(@Param("poList") List<FactoryPlannedTaskPO> poList);

    int update(@Param("po") FactoryPlannedTaskPO po, @Param("operatorId") long operatorId);

    FactoryPlannedTaskPO getById(@Param("id") long id);

    List<FactoryPlannedTaskPO> listByFactoryId(@Param("factoryId") long factoryId,
                                               @Param("starTime") LocalDateTime starTime,
                                               @Param("endTime") LocalDateTime endTime);

    List<FactoryPlannedTaskPO> listByFactoryIdList(@Param("factoryIds") List<Long> factoryIds,
                                                   @Param("starTime") LocalDateTime starTime,
                                                   @Param("endTime") LocalDateTime endTime);

    List<FactoryPlannedTaskPO> listByOrderId(@Param("orderId") long orderId);

    List<FactoryPlannedTaskPO> listByOrderIdList(@Param("orderIds") List<Long> orderIds);

    List<FactoryPlannedTaskPO> listByIds(@Param("ids") List<Long> idList);

    List<FactoryPlannedTaskPO> listByStatus(@Param("instituteId") long instituteId, @Param("statusList") List<Integer> statusList);

    List<FactoryPlannedTaskPO> listByFactoryAndStatus(@Param("factoryId") long factoryId, @Param("statusList") List<Integer> statusList);

}
