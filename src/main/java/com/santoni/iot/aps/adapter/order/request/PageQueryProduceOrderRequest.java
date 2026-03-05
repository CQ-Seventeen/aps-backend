package com.santoni.iot.aps.adapter.order.request;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryProduceOrderRequest extends CommonPageRequest {

    private String orderCode;

    private String customerCode;

    private List<Integer> status;
}
