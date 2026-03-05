package com.santoni.iot.aps.application.bom.dto;

import lombok.Data;

import java.util.List;

@Data
public class StyleDTO {

    private Long id;

    private String produceOrderId;

    private String produceOrderCode;

    private String outerStyleId;

    private String code;

    private String symbolId;

    private String symbol;

    private String name;

    private String description;

    private List<String> imgUrls;

    private List<StyleSkuDTO> skuList;
}
