package com.santoni.iot.aps.application.resource.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MachineFeatureQuery {

    private String attrCode;

    private List<String> attrValueList;
}
