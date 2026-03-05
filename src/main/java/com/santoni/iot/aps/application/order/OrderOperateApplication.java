package com.santoni.iot.aps.application.order;

import com.santoni.iot.aps.application.order.command.*;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;

public interface OrderOperateApplication {

    void createWeavingOrder(CreateWeavingOrderCommand cmd);

    void batchCreateWeavingOrder(BatchCreateWeavingOrderCommand cmd);

    void updateWeavingOrder(UpdateWeavingOrderCommand cmd);

    void createProduceOrder(CreateProduceOrderCommand cmd);

    BatchOperateResultDTO batchCreateProduceOrder(BatchCreateProduceOrderCommand cmd);

    void updateProduceOrder(UpdateProduceOrderCommand cmd);

    void importOuterProduceOrder(OperateOuterProduceOrderCommand cmd);

}
