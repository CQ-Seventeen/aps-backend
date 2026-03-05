package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncProduceOrderReq {

    private Integer operation;

    private String manufactureOrder;

    private String manufactureOrderId;

    private String manufactureBatch;

    private String organizationId;

    private String organizationName;

    private String manufactureDate;

    private String deliveryDate;

    private List<SyncProduceStyleReq> styleList;
}
