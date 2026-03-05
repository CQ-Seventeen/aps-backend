package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

@Data
public class SyncProductItemReq {

    private String productType;

    private String productId;

    private String productName;

    private String productCode;

    private String packageUnit;

    private String batch;

    private String supplierCode;

    private String twist;

    private String color;

    private String description;
}

