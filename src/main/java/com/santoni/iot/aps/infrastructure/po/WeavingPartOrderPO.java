package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeavingPartOrderPO {

    private Long id;

    private long instituteId;

    private Long factoryId;

    private Long weavingOrderId;

    private long produceOrderId;

    private String produceOrderCode;

    private long styleComponentId;

    private String styleCode;

    private String symbolId;

    private String symbol;

    private String skuCode;

    private String sizeId;

    private String size;

    private String partId;

    private String part;

    private String colorId;

    private String color;

    private int quantity;

    private int plannedQuantity;

    private LocalDateTime finishTime;

    private String program;

    private String taskDetailId;

    private String figure;

    private String unit;

    private String comment;

    private int status;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;

}
