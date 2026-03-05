package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.WeavingPartOrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeavingPartOrderMapper {

    int insert(@Param("po") WeavingPartOrderPO po);

    void batchInsert(@Param("poList") List<WeavingPartOrderPO> poList);

    int update(@Param("po") WeavingPartOrderPO po);

    List<WeavingPartOrderPO> listByProduceOrderId(@Param("produceOrderId") long produceOrderId);

    WeavingPartOrderPO getById(@Param("id") long id);

    List<WeavingPartOrderPO> listById(@Param("idList") List<Long> idList);

    List<WeavingPartOrderPO> listByStatus(@Param("instituteId") long instituteId,
                                          @Param("factoryId") long factoryId,
                                          @Param("statusList") List<Integer> statusList);

    List<WeavingPartOrderPO> listByProduceOrderIds(@Param("produceOrderIds") List<Long> produceOrderIds);

    List<WeavingPartOrderPO> listByWeavingOrderIds(@Param("weavingOrderIds") List<Long> weavingOrderIds);

    List<WeavingPartOrderPO> listUnFinishPlanOrders(@Param("instituteId") long instituteId);

}
