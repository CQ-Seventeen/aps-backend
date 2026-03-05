package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

@Data
public class AdviceComplementRequest {

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
