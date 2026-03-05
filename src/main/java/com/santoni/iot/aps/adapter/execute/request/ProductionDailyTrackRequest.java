package com.santoni.iot.aps.adapter.execute.request;

import lombok.Data;

@Data
public class ProductionDailyTrackRequest {

    private String date;

    private long factoryId;
}
