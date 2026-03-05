package com.santoni.iot.aps.application.bom.command;

import lombok.Data;

import java.util.List;

@Data
public class CreateStyleCommand {

    private String outerProduceOrderId;

    private String outerProduceOrderCode;

    private String outerId;

    private String symbolId;

    private String symbol;

    private String code;

    private String name;

    private String description;

    private List<String> imgUrls;

    private List<OperateStyleSkuCommand> skuList;
}
