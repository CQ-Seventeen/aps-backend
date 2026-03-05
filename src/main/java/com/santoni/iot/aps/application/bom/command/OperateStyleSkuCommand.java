package com.santoni.iot.aps.application.bom.command;

import lombok.Data;

import java.util.List;

@Data
public class OperateStyleSkuCommand {

    private String sizeId;

    private String size;

    private List<OperateStyleComponentCommand> components;
}
