package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.CustomerPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchCustomerQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {

    int insert(@Param("po") CustomerPO po);

    void batchInsert(@Param("poList") List<CustomerPO> poList);

    int update(@Param("po") CustomerPO po);

    void batchUpdate(@Param("poList") List<CustomerPO> poList);

    List<CustomerPO> listByCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    CustomerPO getByCode(@Param("instituteId") long instituteId, @Param("code") String code);

    IPage<CustomerPO> searchCustomer(IPage<CustomerPO> page, @Param("instituteId") long instituteId, @Param("qo") SearchCustomerQO qo);
}
