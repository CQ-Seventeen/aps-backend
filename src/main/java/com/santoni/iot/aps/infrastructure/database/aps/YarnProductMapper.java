package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.YarnProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YarnProductMapper {

    int insert(@Param("po") YarnProductPO po);

    int batchInsert(@Param("poList") List<YarnProductPO> poList);

    int update(@Param("po") YarnProductPO po, @Param("operatorId") Long operatorId);

    YarnProductPO getById(@Param("id") Long id);

    YarnProductPO getByYarnCode(@Param("yarnCode") String yarnCode);

    List<YarnProductPO> listByYarnCodes(@Param("yarnCodes") List<String> yarnCodes);

    List<YarnProductPO> listAll();
}

