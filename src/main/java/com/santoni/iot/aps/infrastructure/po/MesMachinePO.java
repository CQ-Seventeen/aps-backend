package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
public class MesMachinePO {

    private Long id;

    private String outerId;

    private String machineCode;

    private String machineNumber;

    private String machineType;

    private String area;

    private String cylinderDiameter;

    private String needleSpacing;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private long deleted;
}
