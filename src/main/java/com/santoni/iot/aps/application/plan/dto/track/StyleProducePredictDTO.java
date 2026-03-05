package com.santoni.iot.aps.application.plan.dto.track;

import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import lombok.Data;

import java.util.List;

@Data
public class StyleProducePredictDTO {

    private StyleComponentDTO styleComponent;

    private int prevDayProduction;

    private int predictProduction;

    private List<YarnPredictDTO> yarnPredicts;
}
