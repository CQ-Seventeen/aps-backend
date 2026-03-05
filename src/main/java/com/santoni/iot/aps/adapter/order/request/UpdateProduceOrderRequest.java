package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateProduceOrderRequest extends CreateProduceOrderRequest {

    private long orderId;
}
