package com.santoni.iot.aps.infrastructure.database.aps;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CodeGeneratorMapper {

    long lock(@Param("type") String type, @Param("instituteId") long instituteId);

    void updateAtomically(@Param("instituteId") long instituteId, @Param("type") String type, @Param("number") long number);
}
