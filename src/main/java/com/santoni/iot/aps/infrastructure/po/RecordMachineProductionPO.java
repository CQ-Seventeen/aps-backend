package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordMachineProductionPO {

    private Long id;

    private long instituteId;

    private Long factoryId;

    private Long taskId;

    private String produceOrderCode;

    private String skuCode;

    private String part;

    private String deviceId;

    private String date;

    private int quantity;

    private int defectQuantity;

    private int inspectQuantity;

    private int inspectDefectQuantity;

    private String barCode;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private long deletedAt;
}
