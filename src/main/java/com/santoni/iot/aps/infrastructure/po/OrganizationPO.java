package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationPO {

    private Long id;

    private String code;

    private long parentId;

    private int level;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;

}
