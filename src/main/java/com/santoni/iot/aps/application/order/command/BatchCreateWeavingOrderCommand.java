package com.santoni.iot.aps.application.order.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BatchCreateWeavingOrderCommand {

    private long produceOrderId;

    private List<CreateWeavingOrderCommand> weavingOrderList;
}
