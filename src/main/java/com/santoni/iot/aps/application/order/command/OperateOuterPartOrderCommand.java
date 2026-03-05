package com.santoni.iot.aps.application.order.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OperateOuterPartOrderCommand {

    private String styleId;

    private String styleName;

    private String styleCode;

    private String symbolId;

    private String symbol;

    private String part;

    private String partId;

    private String program;

    private String partColorId;

    private String partColor;

    private String sizeId;

    private String size;

    private String figure;

    private Integer orderQuantity;

    private String unit;

    private String comment;

    private String taskDetailId;

}
