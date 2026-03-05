package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

@Data
public class SyncProducePartReq {

    private String part;

    private String partId;

    private String program;

    private String partColorId;

    private String partColor;

    private String sizeId;

    private String size;

    private String figure;

    private Integer orderQuantity;

    private String unit;

    private String comment;

    private String taskDtlId;
}
