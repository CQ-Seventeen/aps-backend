package com.santoni.iot.aps.application.bom.dto;

import com.santoni.iot.aps.application.resource.dto.MachineFeatureDTO;
import lombok.Data;

import java.util.List;

@Data
public class MachineRequirementDTO {

    private List<String> typeList;

    private List<String> bareSpandexList;

    private List<MachineFeatureDTO> featureList;
}
