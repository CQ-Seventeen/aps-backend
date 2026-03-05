package com.santoni.iot.aps.application.order.command;

import lombok.Data;

import java.util.List;

@Data
public class BatchCreateProduceOrderCommand {

    private List<CreateProduceOrderCommand> orderList;
}
