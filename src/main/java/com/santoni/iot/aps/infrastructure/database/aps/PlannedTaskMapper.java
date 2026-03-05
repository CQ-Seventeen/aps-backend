package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.PlannedTaskPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchPlannedTaskQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PlannedTaskMapper {

    int insert(@Param("po") PlannedTaskPO po);

    void batchInsert(@Param("poList") List<PlannedTaskPO> poList);

    int update(@Param("po") PlannedTaskPO po, @Param("operatorId") long operatorId);

    int updateStatus(@Param("id") long id,
                     @Param("prevStatus") int prevStatus,
                     @Param("curStatus") int curStatus);

    ;

    PlannedTaskPO getById(@Param("id") long id);

    List<PlannedTaskPO> listByMachineId(@Param("machineId") long machineId,
                                        @Param("starTime") LocalDateTime starTime,
                                        @Param("endTime") LocalDateTime endTime);

    List<PlannedTaskPO> listByMachineIdList(@Param("machineIds") List<Long> machineIds,
                                            @Param("starTime") LocalDateTime starTime,
                                            @Param("endTime") LocalDateTime endTime);

    List<PlannedTaskPO> listByWeavingPartOrderId(@Param("weavingPartOrderId") long weavingPartOrderId);

    List<PlannedTaskPO> listByWeavingPartOrderIds(@Param("weavingPartOrderIds") List<Long> weavingPartOrderIds);

    List<PlannedTaskPO> listByIds(@Param("ids") List<Long> id);

    IPage<PlannedTaskPO> searchTask(IPage<PlannedTaskPO> page, @Param("qo") SearchPlannedTaskQO qo);

    List<PlannedTaskPO> listByMachineAndEndTime(@Param("machineIds") List<Long> machineIds,
                                                @Param("endTimeStart") LocalDateTime starTime,
                                                @Param("endTimeEnd") LocalDateTime endTime);

    List<PlannedTaskPO> listByStatus(@Param("statusList") List<Integer> statusList);

    List<PlannedTaskPO> listByFactoryIdAndStatus(@Param("factoryId") long factoryId,
                                                 @Param("statusList") List<Integer> statusList);

    List<PlannedTaskPO> listByProduceOrderCode(@Param("orderCodeList") List<String> orderCodeList);

    List<PlannedTaskPO> listByTime(@Param("startTime") LocalDateTime starTime,
                                   @Param("endTime") LocalDateTime endTime);

    PlannedTaskPO find(@Param("machineId") long machineId,
                       @Param("orderCode") String orderCode,
                       @Param("styLeCode") String styleCode,
                       @Param("size") String size,
                       @Param("part") String part);


}
