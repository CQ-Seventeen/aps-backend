package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncBomReq {

    private Integer operation;

    private String manufactureOrder;

    private String manufactureOrderId;

    private String styleId;

    private String styleCode;

    private String styleName;

    private String customerStyle;

    private String symbol;

    private String symbolId;

    private List<SyncBomPartReq> partList;
}
