package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StyleSkuPO {

    private Long id;

    private long instituteId;

    private String produceOrderCode;

    private String styleCode;

    private String code;

    private String sizeId;

    private String size;

    private double expectedProduceTime;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
