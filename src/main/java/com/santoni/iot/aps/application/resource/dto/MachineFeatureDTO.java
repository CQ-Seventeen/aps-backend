package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;

import java.util.List;

@Data
public class MachineFeatureDTO {

    private String attrCode;

    private List<String> attrValues;
}
