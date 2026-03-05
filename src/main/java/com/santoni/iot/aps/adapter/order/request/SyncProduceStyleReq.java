package com.santoni.iot.aps.adapter.order.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncProduceStyleReq {

    private String styleId;

    private String styleName;

    private String styleCode;

    private String symbolId;

    private String symbol;

    private List<SyncProducePartReq> partList;
}
