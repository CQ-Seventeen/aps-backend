package com.santoni.iot.aps.application.resource.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OperateMachineAttrConfigCommand {

    private String attrCode;

    private List<String> attrValueList;

    private int optionType;

    private boolean useToFilter;
}
