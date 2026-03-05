package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.MachineAttrConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MachineAttrConfigMapper {

    int insert(@Param("po") MachineAttrConfigPO po);

    void batchInsert(@Param("poList") List<MachineAttrConfigPO> poList);

    int update(@Param("po") MachineAttrConfigPO po, @Param("operatorId") long operatorId);

    MachineAttrConfigPO getByAttrCode(@Param("instituteId") long instituteId, @Param("attrCode") String attrCode);

    List<MachineAttrConfigPO> listByCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    List<MachineAttrConfigPO> listAll(@Param("instituteId") long instituteId);

    List<MachineAttrConfigPO> listFilterAttr(@Param("instituteId") long instituteId);


}
