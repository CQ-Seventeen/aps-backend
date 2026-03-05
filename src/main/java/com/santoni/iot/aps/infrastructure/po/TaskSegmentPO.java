package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaskSegmentPO extends BasePO {

    private Long id;

    private long taskId;

    private int plannedQuantity;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private int status;

    private int sortIndex;
}
