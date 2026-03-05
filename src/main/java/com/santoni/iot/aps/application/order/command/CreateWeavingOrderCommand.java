package com.santoni.iot.aps.application.order.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateWeavingOrderCommand {

    private long factoryId;

    private String code;

    private long produceOrderId;

    private String skuCode;

    private int quantity;

    private LocalDateTime deliveryTime;

    private String program;

    private String taskDetailId;

    private String figure;

    private String unit;

    private String comment;
}
