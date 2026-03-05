package com.santoni.iot.aps.infrastructure.database.mes;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.MesMachinePO;
import com.santoni.iot.aps.infrastructure.po.assistance.MesSizePO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchMachineQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MesMachineMapper {

    MesMachinePO getById(@Param("id") long id);

    List<MesMachinePO> listById(@Param("ids") List<Long> ids);

    MesMachinePO getByDeviceId(@Param("deviceId") String deviceId);

    List<MesMachinePO> listByOptions(@Param("qo") SearchMachineQO qo, @Param("factoryId") Long factoryId);

    IPage<MesMachinePO> searchMachine(IPage<MesMachinePO> page, @Param("qo") SearchMachineQO qo);

    List<MesMachinePO> listAll();

    List<String> getAllMachineType();

    List<MesSizePO> getAllSize();

    List<String> getAllArea();
}
