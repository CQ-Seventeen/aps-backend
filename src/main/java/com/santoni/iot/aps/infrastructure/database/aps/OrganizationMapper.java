package com.santoni.iot.aps.infrastructure.database.aps;

import com.santoni.iot.aps.infrastructure.po.OrganizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    int insert(@Param("po") OrganizationPO po);

    void batchInsert(@Param("poList") List<OrganizationPO> poList);

    int update(@Param("po") OrganizationPO po);

    OrganizationPO getById(@Param("id") long id);

    List<OrganizationPO> listChildren(@Param("parentId") long parentId);

    List<OrganizationPO> pathFromLeaf(@Param("leafId") long id);

    List<OrganizationPO> listById(@Param("idList") List<Long> idList);

    List<OrganizationPO> listAll(@Param("level") int level);

}
