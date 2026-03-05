package com.santoni.iot.aps.infrastructure.acl.feign.response;

import lombok.Data;

@Data
public class YarnStock {

    private String productId;

    private String productCode;

    private String batch;

    private String supplierCode;

    private String twist;

    private String color;

    private String manufactureOrder;

    private Double stockQuantity;

    private Double transitQuantity;

    private String unit;
}

