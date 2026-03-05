package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.MachinePO;
import com.santoni.iot.aps.infrastructure.po.assistance.SizePO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchMachineQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MachineMapper {

    int insert(@Param("po") MachinePO po);

    void batchInsert(@Param("poList") List<MachinePO> poList);

    int update(@Param("po") MachinePO po);

    MachinePO getById(@Param("id") long id);

    List<MachinePO> listById(@Param("ids") List<Long> ids);

    MachinePO getByDeviceId(@Param("instituteId") long instituteId, @Param("factoryId") long factoryId, @Param("deviceId") String deviceId);

    List<MachinePO> listByOptions(@Param("qo") SearchMachineQO qo, @Param("factoryId") Long factoryId);

    IPage<MachinePO> searchMachine(IPage<MachinePO> page, @Param("qo") SearchMachineQO qo);

    IPage<MachinePO> searchMachineByFactory(IPage<MachinePO> page,
                                            @Param("factoryId") long factoryId,
                                            @Param("qo") SearchMachineQO qo);

    List<String> getAllMachineType(@Param("instituteId") long instituteId);

    List<String> getAllBareSpandexType(@Param("instituteId") long instituteId);

    List<SizePO> getAllSize(@Param("instituteId") long instituteId);

    List<MachinePO> listByInstituteId(@Param("instituteId") long instituteId);

    List<MachinePO> listByFactoryId(@Param("factoryId") long factoryId);
}
