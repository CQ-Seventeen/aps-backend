package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignOrderToFactoryCommand {

    private long produceOrderId;

    private long factoryId;

}
