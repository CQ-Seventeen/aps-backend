package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

@Data
public class StyleWeaveDemandPO {

    private long weavingPartOrderId;

    private String orderCode;

    private String skuCode;

    private String part;

    private int quantity;

    private String startTime;

    private String endTime;
}