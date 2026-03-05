package com.santoni.iot.aps.infrastructure.po;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeavingOrderPO extends BasePO {

    private Long id;

    private long instituteId;

    private long factoryId;

    private String code;

    private long produceOrderId;

    private String produceOrderCode;

    private String styleCode;

    private String skuCode;

    private String sizeId;

    private String size;

    private String symbolId;

    private String symbol;

    private String colorId;

    private String color;

    private int quantity;

    private int plannedQuantity;

    private LocalDateTime finishTime;

    private int status;

}
