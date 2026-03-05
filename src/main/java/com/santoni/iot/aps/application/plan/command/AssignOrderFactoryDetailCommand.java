package com.santoni.iot.aps.application.plan.command;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignOrderFactoryDetailCommand {

    private int cylinderDiameter;

    private double days;

}
