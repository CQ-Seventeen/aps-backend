package com.santoni.iot.aps.application.order.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeavingOrderCommand extends CreateWeavingOrderCommand {

    private long orderId;
}
