package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class FactoryPlannedTaskPO extends BasePO {

    private Long id;

    private long instituteId;

    private long produceOrderId;

    private String produceOrderCode;

    private long factoryId;

    private int type;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private int status;
}
