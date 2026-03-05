package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.StyleComponentPO;
import com.santoni.iot.aps.infrastructure.po.qo.ComponentPairQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StyleComponentMapper {

    int insert(@Param("po") StyleComponentPO po);

    int batchInsert(@Param("poList") List<StyleComponentPO> poList);

    int update(@Param("po") StyleComponentPO po);

    int delete(@Param("po")StyleComponentPO po);

    int deleteBySkuCode(@Param("instituteId") long instituteId, @Param("skuCode")String skuCode);

    List<StyleComponentPO> findBySkuCode(@Param("instituteId") long instituteId, @Param("skuCode") String skuCode);

    List<StyleComponentPO> listBySkuCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    StyleComponentPO getBySkuCodeAndPart(@Param("instituteId") long instituteId, @Param("skuCode") String skuCode, @Param("part") String part);

    StyleComponentPO getByOrderAndSkuCodeAndPart(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode, @Param("skuCode") String skuCode, @Param("part") String part);

    List<StyleComponentPO> listBySkuAndPartPair(@Param("instituteId") long instituteId, @Param("pairList") List<ComponentPairQO> qoList);

    List<StyleComponentPO> listByOrderAndSkuCode(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode, @Param("skuCode") String skuCod);

    List<StyleComponentPO> listByOrderAndSkuCodeList(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode, @Param("codeList") List<String> codeList);

}
