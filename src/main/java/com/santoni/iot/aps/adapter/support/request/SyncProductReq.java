package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncProductReq {

    private Integer operation;

    private List<SyncProductItemReq> dataList;
}

