package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.WeavingOrderPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchWeavingOrderQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeavingOrderMapper {

    int insert(@Param("po") WeavingOrderPO po);

    void batchInsert(@Param("poList") List<WeavingOrderPO> poList);

    int update(@Param("po") WeavingOrderPO po, @Param("operatorId") long operatorId);

    WeavingOrderPO getById(@Param("id") long id);

    WeavingOrderPO getByCode(@Param("instituteId") long instituteId, @Param("code") String code);

    List<WeavingOrderPO> listByProduceOrderId(@Param("produceOrderId") long produceOrderId);

    List<WeavingOrderPO> listByCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    IPage<WeavingOrderPO> searchWeavingOrder(IPage<WeavingOrderPO> page, @Param("qo") SearchWeavingOrderQO qo);

    List<WeavingOrderPO> listByStatus(@Param("instituteId") long instituteId, @Param("statusList") List<Integer> statusList);

    List<WeavingOrderPO> listByProduceOrderIds(@Param("produceOrderIds") List<Long> produceOrderIds);

    List<WeavingOrderPO> listByIdList(@Param("idList") List<Long> idList);

}
