package com.santoni.iot.aps.domain.order.repository;

import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.PageData;

import java.util.Collection;
import java.util.List;

public interface WeavingOrderRepository {

    void saveWeavingOrderList(List<WeavingOrder> weavingOrderList);

    WeavingOrderId saveWeavingOrder(WeavingOrder weavingOrder);

    WeavingOrder weavingOrderDetailById(WeavingOrderId weavingOrderId);

    WeavingOrder weavingOrderDetailByCode(WeavingOrderCode weavingOrderCode);

    List<WeavingOrder> listWeavingOrderByProduceOrderId(ProduceOrderId produceOrderId);

    List<WeavingOrder> listWeavingOrderByCode(List<WeavingOrderCode> codeList);

    PageData<WeavingOrder> pageQueryWeavingOrder(SearchWeavingOrder search);

    List<WeavingOrder> listWeavingOrderByProduceOrderIds(Collection<ProduceOrderId> produceOrderIds);

    List<WeavingOrder> listWeavingOrderById(Collection<WeavingOrderId> idList);

    void updatePartOrder(WeavingPartOrder weavingPartOrder);

    void batchSavePartOrder(List<WeavingPartOrder> weavingPartOrderList);

    List<WeavingPartOrder> listPartOrderByProduceOrderIds(List<ProduceOrderId> produceOrderIds);

    List<WeavingPartOrder> listPartOrderByWeavingOrderIds(List<WeavingOrderId> weavingOrderIds);

    List<WeavingPartOrder> listUnFinishPlanPartOrder();

    List<WeavingPartOrder> listWeavingPartOrderById(Collection<WeavingPartOrderId> idList);

    WeavingPartOrder getPartOrderById(WeavingPartOrderId orderId);

}
