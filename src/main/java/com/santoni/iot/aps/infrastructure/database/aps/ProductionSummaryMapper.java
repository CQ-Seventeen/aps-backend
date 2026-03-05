package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.ProductionSummaryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductionSummaryMapper {

    int insert(@Param("po") ProductionSummaryPO po);

    int batchInsert(@Param("poList") List<ProductionSummaryPO> poList);

    int update(@Param("po") ProductionSummaryPO po);

    ProductionSummaryPO findByFactoryAndKeyAndDate(@Param("instituteId") long instituteId,
                                                   @Param("factoryId") long factoryId,
                                                   @Param("date") String date,
                                                   @Param("summaryKey") String summaryKey,
                                                   @Param("type") int type);

    ProductionSummaryPO findLatestByFactoryAndKey(@Param("instituteId") long instituteId,
                                                  @Param("factoryId") long factoryId,
                                                  @Param("summaryKey") String summaryKey,
                                                  @Param("type") int type);

    List<ProductionSummaryPO> listByFactoryAndKeyAndDate(@Param("instituteId") long instituteId,
                                                         @Param("factoryId") long factoryId,
                                                         @Param("date") String date,
                                                         @Param("keyList") List<String> keyList,
                                                         @Param("type") int type);

    List<ProductionSummaryPO> listLatestByFactoryAndKeyList(@Param("instituteId") long instituteId,
                                                            @Param("factoryId") long factoryId,
                                                            @Param("keyList") List<String> keyList,
                                                            @Param("type") int type);

    List<ProductionSummaryPO> listByFactoryAndKeyTypeAndDate(@Param("instituteId") long instituteId,
                                                             @Param("factoryId") long factoryId,
                                                             @Param("date") String date,
                                                             @Param("type") int type);

    List<ProductionSummaryPO> listLatestByKeyList(@Param("instituteId") long instituteId,
                                                  @Param("keyList") List<String> keyList,
                                                  @Param("type") int type);

    List<ProductionSummaryPO> listByKeyTypeAndDate(@Param("instituteId") long instituteId,
                                                   @Param("date") String date,
                                                   @Param("type") int type);

    List<ProductionSummaryPO> listByKeyListAndDate(@Param("instituteId") long instituteId,
                                                   @Param("keyList") List<String> keyList,
                                                   @Param("date") String date);

    List<ProductionSummaryPO> listLatestByKeyListAndDate(@Param("instituteId") long instituteId,
                                                         @Param("keyList") List<String> keyList,
                                                         @Param("date") String date);

}
