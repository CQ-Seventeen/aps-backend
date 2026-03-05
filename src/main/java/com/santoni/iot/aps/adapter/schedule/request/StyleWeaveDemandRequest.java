package com.santoni.iot.aps.adapter.schedule.request;

import lombok.Data;

@Data
public class StyleWeaveDemandRequest {

    private long weavingPartOrderId;

    private int quantity;

}
