package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;

import java.util.List;

@Data
public class MachineAttrConfigDTO {

    private String attrCode;

    private List<String> optionalValues;

    private int optionType;

    private boolean useToFilter;
}
