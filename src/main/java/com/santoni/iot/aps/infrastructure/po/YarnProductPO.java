package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class YarnProductPO extends BasePO {

    private Long id;

    private long instituteId;

    private String outerId;

    private String yarnCode;

    private String yarnName;

    private String packageUnit;

    private String batch;

    private String supplierCode;

    private String twist;

    private String colorId;

    private String color;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}

