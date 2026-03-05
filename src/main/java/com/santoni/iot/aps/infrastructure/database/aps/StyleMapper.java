package com.santoni.iot.aps.infrastructure.database.aps;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.santoni.iot.aps.infrastructure.po.StylePO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchStyleQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StyleMapper {

    int insert(@Param("po") StylePO po);

    void batchInsert(@Param("poList") List<StylePO> poList);

    int update(@Param("po") StylePO po);

    void batchUpdate(@Param("poList") List<StylePO> poList);

    List<StylePO> listByCodeList(@Param("instituteId") long instituteId, @Param("codeList") List<String> codeList);

    StylePO getByCode(@Param("instituteId") long instituteId, @Param("code") String code);

    StylePO getByOrderAndCode(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode,  @Param("code") String code);

    StylePO getById(@Param("id") long id);

    IPage<StylePO> searchStyle(IPage<StylePO> page, @Param("instituteId") long instituteId, @Param("qo") SearchStyleQO qo);

    StylePO lockByOrderAndCode(@Param("instituteId") long instituteId, @Param("orderCode") String orderCode,  @Param("code") String code);
}
