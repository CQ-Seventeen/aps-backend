package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.ProduceOrderDemandPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProduceOrderDemandMapper {

    int insert(@Param("po") ProduceOrderDemandPO po);

    void batchInsert(@Param("poList") List<ProduceOrderDemandPO> poList);

    int update(@Param("po") ProduceOrderDemandPO po);

    void batchUpdate(@Param("poList") List<ProduceOrderDemandPO> poList);

    void batchDelete(@Param("poList") List<ProduceOrderDemandPO> poList);

    List<ProduceOrderDemandPO> listByProduceOrderId(@Param("produceOrderId") long produceOrderId);

    ProduceOrderDemandPO getByStyleCodeAndOrder(@Param("styleCode") String styleCode, @Param("produceOrderId") long produceOrderId);

    List<ProduceOrderDemandPO> listByProduceOrderIds(@Param("produceOrderIds") List<Long> produceOrderIds);
}
