package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlannedTaskPO extends BasePO {

    private Long id;

    private long instituteId;

    private String taskCode;

    private Long factoryId;

    private long weavingPartOrderId;

    private String produceOrderCode;

    private long machineId;

    private String styleCode;

    private String symbolId;

    private String symbol;

    private String sizeId;

    private String size;

    private String skuCode;

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int producedQuantity;

    private int plannedQuantity;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private LocalDateTime estimateEndTime;

    private int status;

    private String flag;

}
