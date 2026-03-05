package com.santoni.iot.aps.application.plan.dto.track;

import lombok.Data;

import java.util.List;

@Data
public class ProducePredictDTO {

    private List<StyleProducePredictDTO> styleProducePredicts;

    private List<YarnPredictDTO> yarnPredicts;
}
