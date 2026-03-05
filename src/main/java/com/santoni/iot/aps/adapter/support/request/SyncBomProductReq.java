package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

@Data
public class SyncBomProductReq {

    private String productCode;

    private String batch;

    private String twist;

    private String color;

    private String supplierCode;

    private String reachingMethod;

    private Double percentage;
}

