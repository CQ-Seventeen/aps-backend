package com.santoni.iot.aps.application.order;

import com.santoni.iot.aps.application.order.dto.OverviewOrderDTO;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.order.dto.WeavingOrderDTO;
import com.santoni.iot.aps.application.order.query.*;
import com.santoni.iot.aps.application.plan.dto.factory.ProduceOrderDemandDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;

import java.util.List;

public interface OrderQueryApplication {

    PageResult<WeavingOrderDTO> pageQueryWeavingOrder(PageWeavingOrderQuery query);

    PageResult<ProduceOrderDTO> pageQueryProduceOrder(ProduceOrderQuery query);

    List<WeavingOrderDTO> listWeavingOrderById(ListWeavingOrderByIdQuery query);

    ProduceOrderDTO queryProduceOrderByCode(ProduceOrderDetailByCodeQuery query);

    OverviewOrderDTO overviewOrder();

    ProduceOrderDemandDTO queryProduceOrderDemand(ProduceOrderDetailQuery query);

    List<Long> canWeaveMachine(CanWeaveMachineQuery query);

}
