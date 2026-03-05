package com.santoni.iot.aps.application.execute;

import com.santoni.iot.aps.application.resource.dto.EstimateTimeDTO;
import com.santoni.iot.aps.application.execute.query.EstimateMachineWeaveTimeQuery;
import com.santoni.iot.aps.application.execute.query.EstimateMaxWaveQuantityQuery;

public interface ExecuteQueryApplication {

    EstimateTimeDTO estimateWeaveTime(EstimateMachineWeaveTimeQuery query);

    int estimateMaxWeaveQuantity(EstimateMaxWaveQuantityQuery query);

}
