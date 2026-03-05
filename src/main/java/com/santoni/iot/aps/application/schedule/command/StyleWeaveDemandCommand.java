package com.santoni.iot.aps.application.schedule.command;

import lombok.Data;

@Data
public class StyleWeaveDemandCommand {

    private long weavingPartOrderId;

    private int quantity;
}
