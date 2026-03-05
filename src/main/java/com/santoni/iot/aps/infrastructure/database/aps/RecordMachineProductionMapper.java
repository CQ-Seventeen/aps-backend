package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.RecordMachineProductionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecordMachineProductionMapper {

    int insert(@Param("po") RecordMachineProductionPO po);

    int batchInsert(@Param("poList") List<RecordMachineProductionPO> poList);

    int update(@Param("po") RecordMachineProductionPO po);

    List<RecordMachineProductionPO> listByFactoryAndDate(@Param("instituteId") long instituteId,
                                                         @Param("factoryId") long factoryId,
                                                         @Param("date") String date);

    RecordMachineProductionPO findByRecord(@Param("po") RecordMachineProductionPO po);

    List<RecordMachineProductionPO> listOldestSkuRecordByOrder(@Param("instituteId") long instituteId,
                                                               @Param("orderCodeList") List<String> orderCodeList);

    int batchDelete(@Param("ids") List<Long> ids);

    List<RecordMachineProductionPO> listByDate(@Param("instituteId") long instituteId,
                                               @Param("date") String date);


    List<RecordMachineProductionPO> listByTaskId(@Param("instituteId") long instituteId,
                                                 @Param("taskId") long taskId);

    RecordMachineProductionPO findByBarCode(@Param("instituteId") long instituteId, @Param("barCode") String barCode);

}
