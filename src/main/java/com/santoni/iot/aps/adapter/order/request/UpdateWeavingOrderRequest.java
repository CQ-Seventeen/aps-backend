package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeavingOrderRequest extends CreateWeavingOrderRequest {

    private long orderId;
}
