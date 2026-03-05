package com.santoni.iot.aps.application.bom.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperateOuterYarnCommand extends UpdateStyleCommand {

    private String productType;

    private String productId;

    private String productName;

    private String productCode;

    private String packageUnit;

    private String batch;

    private String supplierCode;

    private String twist;

    private String color;

    private String description;
}
