package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;

import java.util.List;

@Data
public class OperateMachineAttrConfigRequest {

    private String attrCode;

    private List<String> optionalValues;

    private int optionType;

    private boolean useToFilter;
}
