package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BasePO {

    private long creatorId;

    private long operatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
