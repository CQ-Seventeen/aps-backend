package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MachineRequirementPO {

    private List<String> typeList;

    private List<String> bareSpandexList;

    private Boolean highSpeed;

    private Map<String, List<String>> featureMap;
}
