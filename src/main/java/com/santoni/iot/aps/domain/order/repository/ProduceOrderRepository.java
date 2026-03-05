package com.santoni.iot.aps.domain.order.repository;

import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.PageData;

import java.util.Collection;
import java.util.List;

public interface ProduceOrderRepository {

    ProduceOrderId saveProduceOrder(ProduceOrder produceOrder);

    ProduceOrder produceOrderDetailById(ProduceOrderId produceOrderId);

    ProduceOrder produceOrderDetailByCode(ProduceOrderCode produceOrderCode);

    PageData<ProduceOrder> pageQueryProduceOrder(SearchProduceOrder search);

    List<ProduceOrder> unfinishedProduceOrder();

    List<ProduceOrder> listProduceOrderById(Collection<ProduceOrderId> idList);

    List<ProduceOrder> listProduceOrderByCode(Collection<ProduceOrderCode> codeList);
}
