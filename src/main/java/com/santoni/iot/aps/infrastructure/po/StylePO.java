package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StylePO {

    private Long id;

    private long instituteId;

    private String produceOrderId;

    private String produceOrderCode;

    private String styleId;

    private String code;

    private String symbolId;

    private String symbol;

    private String name;

    private String description;

    private String images;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
