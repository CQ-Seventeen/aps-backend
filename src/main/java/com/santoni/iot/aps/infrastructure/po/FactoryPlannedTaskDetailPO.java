package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FactoryPlannedTaskDetailPO {

    private Long id;

    private long taskId;

    private int cylinderDiameter;

    private int machineNumber;

    private double occupiedDays;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
