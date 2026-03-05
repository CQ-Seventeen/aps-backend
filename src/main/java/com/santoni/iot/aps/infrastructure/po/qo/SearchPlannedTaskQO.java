package com.santoni.iot.aps.infrastructure.po.qo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchPlannedTaskQO {

    private long instituteId;

    private Long factoryId;

    private String produceOrderCode;

    private String styleCode;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
