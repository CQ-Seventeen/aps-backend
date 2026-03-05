package com.santoni.iot.aps.application.bom.dto;

import lombok.Data;

import java.util.List;

@Data
public class StyleSkuDTO {

    private String styleCode;

    private String sizeId;

    private String size;

    private String skuCode;

    private List<StyleComponentDTO> components;

}
