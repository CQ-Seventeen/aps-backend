package com.santoni.iot.aps.application.schedule.dto;

import lombok.Data;

@Data
public class StyleWeaveDemandDTO {

    private long weavingPartOrderId;

    private String styleCode;

    private String part;

    private int quantity;

    private String deliveryTime;
}
