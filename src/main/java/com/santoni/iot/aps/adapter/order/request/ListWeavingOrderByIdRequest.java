package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

import java.util.List;

@Data
public class ListWeavingOrderByIdRequest {

    private List<Long> weavingOrderIds;
}
