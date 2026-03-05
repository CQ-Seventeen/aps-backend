package com.santoni.iot.aps.application.order.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateProduceOrderCommand extends CreateProduceOrderCommand {

    private long orderId;
}
