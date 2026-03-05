package com.santoni.iot.aps.application.resource.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MachineFeatureCommand {

    private String attrCode;

    private List<String> attrValueList;
}
