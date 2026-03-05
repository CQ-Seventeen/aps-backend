package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductionSummaryPO {

    private Long id;

    private long instituteId;

    private long factoryId;

    private String sumKey;

    private int type;

    private String date;

    private int quantity;

    private int tdQuantity;

    private int defectQuantity;

    private int tdDefectQuantity;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;
}
