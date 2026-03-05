package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;

import java.util.List;

@Data
public class MachineFeatureRequest {

    private String attrCode;

    private List<String> attrValues;

}
