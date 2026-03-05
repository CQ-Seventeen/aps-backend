package com.santoni.iot.aps.application.execute;

import com.santoni.iot.aps.application.execute.dto.OrderStyleProductionDTO;
import com.santoni.iot.aps.application.execute.dto.ProductionAggregateByMachineDTO;
import com.santoni.iot.aps.application.execute.dto.StyleComponentProductionDTO;
import com.santoni.iot.aps.application.execute.query.FactoryProductionTrackQuery;
import com.santoni.iot.aps.application.execute.query.OrderStyleProductionQuery;

import java.util.List;

public interface ProduceQueryApplication {

    List<StyleComponentProductionDTO> queryDailyProductionTrack(FactoryProductionTrackQuery query);

    List<OrderStyleProductionDTO> queryOrderStyleProduction(OrderStyleProductionQuery query);

    List<ProductionAggregateByMachineDTO> queryProductionAggregateByMachine();
}
