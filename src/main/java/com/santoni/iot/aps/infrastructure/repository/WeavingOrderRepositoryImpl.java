package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.infrastructure.database.aps.WeavingOrderMapper;
import com.santoni.iot.aps.infrastructure.database.aps.WeavingPartOrderMapper;
import com.santoni.iot.aps.infrastructure.factory.OrderFactory;
import com.santoni.iot.aps.infrastructure.po.WeavingOrderPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchWeavingOrderQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class WeavingOrderRepositoryImpl implements WeavingOrderRepository {

    @Autowired
    private WeavingOrderMapper weavingOrderMapper;

    @Autowired
    private WeavingPartOrderMapper weavingPartOrderMapper;

    @Autowired
    private OrderFactory orderFactory;
    @Autowired
    private PathMatcher pathMatcher;

    @Override
    public void saveWeavingOrderList(List<WeavingOrder> weavingOrderList) {
        for (var order : weavingOrderList) {
            saveWeavingOrder(order);
        }
    }

    @Override
    public WeavingOrderId saveWeavingOrder(WeavingOrder weavingOrder) {
        WeavingOrderPO po = orderFactory.convertWeavingOrderEntityToPO(weavingOrder);
        long userId = PlanContext.getUserId();
        if (null == weavingOrder.getId()) {
            po.setCreatorId(userId);
            po.setOperatorId(userId);
            weavingOrderMapper.insert(po);
            return new WeavingOrderId(po.getId());
        } else {
            po.setId(weavingOrder.getId().value());
            weavingOrderMapper.update(po, userId);
            return weavingOrder.getId();
        }
    }

    @Override
    public WeavingOrder weavingOrderDetailById(WeavingOrderId weavingOrderId) {
        var po = weavingOrderMapper.getById(weavingOrderId.value());
        if (null == po) {
            return null;
        }
        return orderFactory.composeWeavingOrder(po);
    }

    @Override
    public WeavingOrder weavingOrderDetailByCode(WeavingOrderCode weavingOrderCode) {
        var po = weavingOrderMapper.getByCode(PlanContext.getInstituteId(), weavingOrderCode.value());
        if (null == po) {
            return null;
        }
        return orderFactory.composeWeavingOrder(po);
    }

    @Override
    public List<WeavingOrder> listWeavingOrderByProduceOrderId(ProduceOrderId produceOrderId) {
        var poList = weavingOrderMapper.listByProduceOrderId(produceOrderId.value());
        return composeWeavingOrderListFromPOList(poList);
    }

    @Override
    public List<WeavingOrder> listWeavingOrderByCode(List<WeavingOrderCode> codeList) {
        var orderCodeList = codeList.stream().map(WeavingOrderCode::value).toList();
        var poList = weavingOrderMapper.listByCodeList(PlanContext.getInstituteId(), orderCodeList);
        return composeWeavingOrderListFromPOList(poList);
    }

    @Override
    public PageData<WeavingOrder> pageQueryWeavingOrder(SearchWeavingOrder search) {
        IPage<WeavingOrderPO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchWeavingOrderQO();
        qo.setInstituteId(PlanContext.getInstituteId());
        if (null != search.getFactoryId()) {
            qo.setFactoryId(search.getFactoryId().value());
        }
        if (null != search.getCode()) {
            qo.setCode(search.getCode().value());
        }
        if (null != search.getStyleCode()) {
            qo.setStyleCode(search.getStyleCode().value());
        }
        if (CollectionUtils.isNotEmpty(search.getStatus())) {
            qo.setStatusList(search.getStatus().stream().map(WeavingOrderStatus::getCode).toList());
        }
        var pageRes = weavingOrderMapper.searchWeavingOrder(page, qo);
        return PageData.fromPage(composeWeavingOrderListFromPOList(pageRes.getRecords()), pageRes);
    }

    @Override
    public List<WeavingOrder> listWeavingOrderByProduceOrderIds(Collection<ProduceOrderId> produceOrderIds) {
        var poList = weavingOrderMapper.listByProduceOrderIds(produceOrderIds.stream().map(ProduceOrderId::value).toList());
        return composeWeavingOrderListFromPOList(poList);
    }

    @Override
    public List<WeavingOrder> listWeavingOrderById(Collection<WeavingOrderId> idList) {
        var poList = weavingOrderMapper.listByIdList(idList.stream().map(WeavingOrderId::value).toList());
        return composeWeavingOrderListFromPOList(poList);
    }

    @Override
    public void updatePartOrder(WeavingPartOrder weavingPartOrder) {
        var po = orderFactory.convertWeavingPartOrderEntityToPO(weavingPartOrder);
        weavingPartOrderMapper.update(po);
    }

    @Override
    public void batchSavePartOrder(List<WeavingPartOrder> weavingPartOrderList) {
        var poList = weavingPartOrderList.stream().map(it -> orderFactory.convertWeavingPartOrderEntityToPO(it)).toList();
        weavingPartOrderMapper.batchInsert(poList);
    }

    @Override
    public List<WeavingPartOrder> listPartOrderByProduceOrderIds(List<ProduceOrderId> produceOrderIds) {
        var poList = weavingPartOrderMapper.listByProduceOrderIds(produceOrderIds.stream().map(ProduceOrderId::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }
        return poList.stream().map(it -> orderFactory.composeWeavingPartOrder(it)).toList();
    }

    @Override
    public List<WeavingPartOrder> listPartOrderByWeavingOrderIds(List<WeavingOrderId> weavingOrderIds) {
        var poList = weavingPartOrderMapper.listByWeavingOrderIds(weavingOrderIds.stream().map(WeavingOrderId::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }
        return poList.stream().map(it -> orderFactory.composeWeavingPartOrder(it)).toList();
    }

    @Override
    public List<WeavingPartOrder> listUnFinishPlanPartOrder() {
        var poList = weavingPartOrderMapper.listUnFinishPlanOrders(PlanContext.getInstituteId());
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }
        return poList.stream().map(it -> orderFactory.composeWeavingPartOrder(it)).toList();
    }

    @Override
    public List<WeavingPartOrder> listWeavingPartOrderById(Collection<WeavingPartOrderId> idList) {
        var orderIds = idList.stream().map(WeavingPartOrderId::value).toList();
        var poList = weavingPartOrderMapper.listById(orderIds);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }
        return poList.stream().map(it -> orderFactory.composeWeavingPartOrder(it)).toList();
    }

    @Override
    public WeavingPartOrder getPartOrderById(WeavingPartOrderId orderId) {
        var po = weavingPartOrderMapper.getById(orderId.value());
        if (null == po) {
            return null;
        }
        return orderFactory.composeWeavingPartOrder(po);
    }

    private List<WeavingOrder> composeWeavingOrderListFromPOList(List<WeavingOrderPO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream()
                .map(it -> orderFactory.composeWeavingOrder(it)).toList();
    }
}
