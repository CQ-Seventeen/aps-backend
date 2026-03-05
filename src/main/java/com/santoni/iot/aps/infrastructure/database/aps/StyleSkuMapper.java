package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.StyleSkuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StyleSkuMapper {

    int insert(@Param("po") StyleSkuPO po);

    int batchInsert(@Param("poList") List<StyleSkuPO> poList);

    int update(@Param("po") StyleSkuPO po);

    int delete(@Param("po") StyleSkuPO po);

    List<StyleSkuPO> findByStyleCode(@Param("instituteId") long instituteId, @Param("styleCode") String styleCode);

    List<StyleSkuPO> listByStyleCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    StyleSkuPO findBySkuCode(@Param("instituteId") long instituteId, @Param("skuCode") String skuCode);

    StyleSkuPO findByOrderAndCode(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode, @Param("skuCode") String skuCode);

    List<StyleSkuPO> listBySkuCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    List<StyleSkuPO> listByOrderAndSkuCode(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode, @Param("codeList") List<String> codeList);

    StyleSkuPO findByStyleAndSize(@Param("instituteId") long instituteId, @Param("styleCode") String styleCode, @Param("size") String size);

    List<StyleSkuPO> listByOrderCodeList(@Param("instituteId") long instituteId, @Param("orderCodeList") List<String> orderCodeList);

}
