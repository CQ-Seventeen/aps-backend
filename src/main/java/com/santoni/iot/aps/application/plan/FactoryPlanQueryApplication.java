package com.santoni.iot.aps.application.plan;

import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.plan.dto.factory.FactoryOrderDimPlanDTO;
import com.santoni.iot.aps.application.plan.dto.factory.MachineCapacityDTO;
import com.santoni.iot.aps.application.plan.query.FactoryOrderQuery;
import com.santoni.iot.aps.application.plan.query.FactoryPlanQuery;
import com.santoni.iot.aps.application.plan.query.MachineCapacityQuery;

import java.util.List;

public interface FactoryPlanQueryApplication {

    FactoryOrderDimPlanDTO queryFactoryPlanOrderDim(FactoryPlanQuery query);

    List<ProduceOrderDTO> queryOrderInFactory(FactoryOrderQuery query);

    List<MachineCapacityDTO> queryFactoryMachineCapacity(MachineCapacityQuery query);
}
