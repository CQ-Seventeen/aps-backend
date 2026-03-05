package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.RecordMachineProductionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MachineProductionDraftMapper {

    int insert(@Param("po") RecordMachineProductionPO po);

    int batchInsert(@Param("poList") List<RecordMachineProductionPO> poList);

    int update(@Param("po") RecordMachineProductionPO po);

    List<RecordMachineProductionPO> listByDate(@Param("instituteId") long instituteId,
                                               @Param("factoryId") long factoryId,
                                               @Param("date") String date);

    RecordMachineProductionPO findByRecord(@Param("po") RecordMachineProductionPO po);


    int batchDelete(@Param("ids") List<Long> ids);

    void deleteByDate(@Param("instituteId") long instituteId,
                      @Param("factoryId") long factoryId,
                      @Param("date") String date);
}
