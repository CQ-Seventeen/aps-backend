package com.santoni.iot.aps.application.order.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OperateOuterProduceOrderCommand {

    private String outOrderId;

    private String code;

    private String orgId;

    private String orgName;

    private String customerCode;

    private List<StyleDemandCommand> styleDemands;

    private LocalDateTime deliveryTime;

    private String manufactureBatch;

    private LocalDateTime manufactureDate;

    private List<OperateOuterPartOrderCommand> partOrders;
}
