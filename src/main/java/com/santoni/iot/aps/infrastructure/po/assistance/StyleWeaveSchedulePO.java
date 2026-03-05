package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StyleWeaveSchedulePO {

    private long weavingPartOrderId;

    private String orderCode;

    private String skuCode;

    private String part;

    private int quantity;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}