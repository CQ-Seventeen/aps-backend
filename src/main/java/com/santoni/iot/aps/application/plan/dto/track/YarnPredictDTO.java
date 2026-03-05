package com.santoni.iot.aps.application.plan.dto.track;

import com.santoni.iot.aps.application.bom.dto.YarnUsageDTO;
import lombok.Data;

@Data
public class YarnPredictDTO {

    private YarnUsageDTO demand;

    private YarnStockDTO stock;
}
