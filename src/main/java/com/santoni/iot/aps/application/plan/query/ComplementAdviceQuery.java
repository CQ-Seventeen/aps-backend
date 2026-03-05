package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

@Data
public class ComplementAdviceQuery {

    private long weavingPartOrderId;

    private String styleCode;

    private String sizeId;

    private String size;

    private String skuCode;

    private String partId;

    private String part;

    private int quantity;

    private String endTime;
}
