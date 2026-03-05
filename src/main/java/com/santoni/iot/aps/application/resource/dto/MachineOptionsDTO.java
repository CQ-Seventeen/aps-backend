package com.santoni.iot.aps.application.resource.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MachineOptionsDTO {

    private Map<Integer, List<Integer>> cylinderDiameterMap;

    private List<Integer> needleSpacingList;

    private List<String> machineTypeList;

    private List<String> bareSpandexTypeList;

    private List<Boolean> highSpeedList;

    private List<MachineFeatureDTO> machineFeatureList;

    private List<String> machineAreaList;
}
