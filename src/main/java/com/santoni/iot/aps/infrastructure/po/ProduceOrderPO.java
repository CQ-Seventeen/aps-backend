package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProduceOrderPO extends BasePO {

    private Long id;

    private String outerId;

    private String orgId;

    private String orgName;

    private String code;

    private long instituteId;

    private String customerCode;

    private String customerName;

    private LocalDateTime deliveryTime;

    private String manufactureBatch;

    private LocalDateTime manufactureDate;

    private int status;

}
