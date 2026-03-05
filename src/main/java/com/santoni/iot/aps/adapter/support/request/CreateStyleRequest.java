package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateStyleRequest {

    private String code;

    private String name;

    private String description;

    private List<String> imgUrls;

    private List<OperateStyleSkuRequest> sizeList;
}
