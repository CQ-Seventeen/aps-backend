package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchProduceOrderQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProduceOrderMapper {

    long insert(@Param("po") ProduceOrderPO po);

    void batchInsert(@Param("poList") List<ProduceOrderPO> poList);

    int update(@Param("po") ProduceOrderPO po, @Param("operatorId") long operatorId);

    ProduceOrderPO getById(@Param("id") long id);

    ProduceOrderPO getByCode(@Param("instituteId") long instituteId, @Param("code") String code);

    List<ProduceOrderPO> listByInstituteId(@Param("instituteId") long instituteId);

    IPage<ProduceOrderPO> searchProduceOrder(IPage<ProduceOrderPO> page, @Param("qo") SearchProduceOrderQO qo);

    List<ProduceOrderPO> listByStatus(@Param("instituteId") long instituteId, @Param("statusList") List<Integer> statusList);

    List<ProduceOrderPO> listByIdList(@Param("idList") List<Long> idList);

    List<ProduceOrderPO> listByCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    IPage<ProduceOrderPO> searchProduceOrderByFactory(IPage<ProduceOrderPO> page, @Param("qo") SearchProduceOrderQO qo, @Param("factoryId") long factoryId);
}
