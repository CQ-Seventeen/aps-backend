package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProduceOrderDemandPO {

    private Long id;

    private long produceOrderId;

    private String styleCode;

    private String skuCode;

    private String sizeId;

    private String size;

    private String symbolId;

    private String symbol;

    private String colorId;

    private String color;

    private int orderQuantity;

    private int weaveQuantity;

    private int sampleQuantity;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;

}
