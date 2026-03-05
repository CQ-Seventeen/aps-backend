package com.santoni.iot.aps.infrastructure.acl.feign.request;

import lombok.Data;

@Data
public class YarnStockQueryRequest {

    private String productId;

    private String productCode;

    private String batch;

    private String supplierCode;

    private String twist;

    private String color;

    private String manufactureOrder;

    private String warehouseId;
}

