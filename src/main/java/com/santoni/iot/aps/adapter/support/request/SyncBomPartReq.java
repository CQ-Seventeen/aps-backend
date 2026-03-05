package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncBomPartReq {

    private String partId;

    private String part;

    private String partColorId;

    private String partColor;

    private String sizeId;

    private String size;

    private Double finishWeight;

    private Integer finishDuration;

    private String machineType;

    private String diameter;

    private String finishWaist;

    private String finishLength;

    private String wastageRate;

    private String program;

    private Integer standardQuantity;

    private String dye;

    private String finishRatio;

    private List<SyncBomProductReq> productList;
}
