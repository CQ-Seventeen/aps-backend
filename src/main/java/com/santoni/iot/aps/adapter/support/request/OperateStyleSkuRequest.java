package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

import java.util.List;

@Data
public class OperateStyleSkuRequest {

    private String sizeId;

    private String size;

    private List<OperateStyleComponentRequest> components;
}
