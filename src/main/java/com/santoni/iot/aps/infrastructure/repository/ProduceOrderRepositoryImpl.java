package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.infrastructure.database.aps.ProduceOrderDemandMapper;
import com.santoni.iot.aps.infrastructure.database.aps.ProduceOrderMapper;
import com.santoni.iot.aps.infrastructure.factory.OrderFactory;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderDemandPO;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchProduceOrderQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProduceOrderRepositoryImpl implements ProduceOrderRepository {

    @Autowired
    private ProduceOrderMapper produceOrderMapper;

    @Autowired
    private ProduceOrderDemandMapper produceOrderDemandMapper;

    @Autowired
    private OrderFactory orderFactory;

    @Override
    public ProduceOrderId saveProduceOrder(ProduceOrder produceOrder) {
        var po = orderFactory.convertProduceOrderEntityToPO(produceOrder);
        var userId = PlanContext.getUserId();
        if (null == produceOrder.getId()) {
            return insertProduceOrder(produceOrder, po, userId);
        } else {
            return updateProduceOrder(produceOrder, po, userId);
        }
    }

    private ProduceOrderId insertProduceOrder(ProduceOrder produceOrder, ProduceOrderPO po, long userId) {
        po.setCreatorId(userId);
        po.setOperatorId(userId);
        produceOrderMapper.insert(po);
        batchInsertStyleDemand(produceOrder.getDemands(), po.getId());
        return new ProduceOrderId(po.getId());
    }

    private ProduceOrderId updateProduceOrder(ProduceOrder produceOrder, ProduceOrderPO po, long userId) {
        po.setId(produceOrder.getId().value());
        produceOrderMapper.update(po, userId);
        saveStyleDemand(produceOrder.getDemands(), po.getId());
        return produceOrder.getId();
    }

    private void batchInsertStyleDemand(List<StyleDemand> styleDemands, long produceOrderId) {
        var poList = orderFactory.convertProduceOrderDemandEntityToPO(styleDemands, produceOrderId);
        produceOrderDemandMapper.batchInsert(poList);
    }

    private void saveStyleDemand(List<StyleDemand> styleDemands, long produceOrderId) {
        var existPOList = produceOrderDemandMapper.listByProduceOrderId(produceOrderId);
        var existPOMap = existPOList.stream()
                .collect(Collectors.groupingBy(ProduceOrderDemandPO::getStyleCode,
                        Collectors.toMap(ProduceOrderDemandPO::getColor, it -> it, (v1, v2) -> v1)));

        List<ProduceOrderDemandPO> toUpdateList = Lists.newArrayList();
        List<ProduceOrderDemandPO> toInsertList = Lists.newArrayList();

        var poList = orderFactory.convertProduceOrderDemandEntityToPO(styleDemands, produceOrderId);
        for (var po : poList) {
            var existStyleCodeMap = existPOMap.get(po.getStyleCode());
            if (MapUtils.isEmpty(existStyleCodeMap)) {
                toInsertList.add(po);
            } else {
                var existPO = existStyleCodeMap.get(po.getColor());
                if (null == existPO) {
                    toInsertList.add(po);
                } else {
                    po.setId(existPO.getId());
                    toUpdateList.add(po);
                    existStyleCodeMap.remove(po.getColor());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(toInsertList)) {
            produceOrderDemandMapper.batchInsert(toInsertList);
        }
        if (CollectionUtils.isNotEmpty(toUpdateList)) {
            produceOrderDemandMapper.batchUpdate(toUpdateList);
        }
        var leftPOList = existPOMap.values().stream().flatMap(it -> it.values().stream()).toList();
        if (CollectionUtils.isNotEmpty(leftPOList)) {
            produceOrderDemandMapper.batchDelete(leftPOList);
        }
    }

    @Override
    public ProduceOrder produceOrderDetailById(ProduceOrderId produceOrderId) {
        var po = produceOrderMapper.getById(produceOrderId.value());
        return composeProduceOrderFromPO(po);
    }

    @Override
    public ProduceOrder produceOrderDetailByCode(ProduceOrderCode produceOrderCode) {
        var po = produceOrderMapper.getByCode(PlanContext.getInstituteId(), produceOrderCode.value());
        return composeProduceOrderFromPO(po);
    }

    private ProduceOrder composeProduceOrderFromPO(ProduceOrderPO po) {
        if (null == po) {
            return null;
        }
        var demandList = produceOrderDemandMapper.listByProduceOrderId(po.getId());
        return orderFactory.composeProduceOrderEntityFromPO(po, demandList);
    }

    @Override
    public PageData<ProduceOrder> pageQueryProduceOrder(SearchProduceOrder search) {
        IPage<ProduceOrderPO> page = new Page<>(search.getPage(), search.getPageSize());
        var qo = new SearchProduceOrderQO();
        qo.setInstituteId(PlanContext.getInstituteId());
        if (null != search.getCode()) {
            qo.setCode(search.getCode().value());
        }
        if (null != search.getCustomerCode()) {
            qo.setCustomerCode(search.getCustomerCode().value());
        }
        if (CollectionUtils.isNotEmpty(search.getStatus())) {
            qo.setStatusList(search.getStatus().stream().map(ProduceOrderStatus::getCode).toList());
        }
        if (null != search.getSearch()) {
            qo.setSearch(search.getSearch().value());
        }
        if (null != search.getStart()) {
            qo.setDeliveryTimeStart(search.getStart().value());
        }
        if (null != search.getEnd()) {
            qo.setDeliveryTimeEnd(search.getEnd().value());
        }
        IPage<ProduceOrderPO> pageRes;
        if (null != search.getFactoryId()) {
            pageRes = produceOrderMapper.searchProduceOrderByFactory(page, qo, search.getFactoryId().value());
        } else {
            pageRes = produceOrderMapper.searchProduceOrder(page, qo);
        }
        return PageData.fromPage(composeProduceOrderListFromPOList(pageRes.getRecords(), true), pageRes);
    }

    @Override
    public List<ProduceOrder> unfinishedProduceOrder() {
        var poList = produceOrderMapper.listByStatus(PlanContext.getInstituteId(), ProduceOrderStatus.getUnfinishedStatus());
        return composeProduceOrderListFromPOList(poList, true);
    }

    @Override
    public List<ProduceOrder> listProduceOrderById(Collection<ProduceOrderId> idList) {
        var poList = produceOrderMapper.listByIdList(idList.stream().map(ProduceOrderId::value).toList());
        return composeProduceOrderListFromPOList(poList, true);
    }

    @Override
    public List<ProduceOrder> listProduceOrderByCode(Collection<ProduceOrderCode> codeList) {
        var poList = produceOrderMapper.listByCodeList(PlanContext.getInstituteId(), codeList.stream().map(ProduceOrderCode::value).toList());
        return composeProduceOrderListFromPOList(poList, false);
    }

    private List<ProduceOrder> composeProduceOrderListFromPOList(List<ProduceOrderPO> poList, boolean needDemand) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        if (!needDemand) {
            return poList.stream()
                    .map(it -> orderFactory.composeProduceOrderEntityFromPO(it, Collections.emptyList()))
                    .toList();
        }
        var orderIds = poList.stream().map(ProduceOrderPO::getId).toList();
        var demandList = produceOrderDemandMapper.listByProduceOrderIds(orderIds);

        var demandMap = demandList.stream().collect(Collectors.groupingBy(ProduceOrderDemandPO::getProduceOrderId));

        return poList.stream()
                .map(it -> orderFactory.composeProduceOrderEntityFromPO(it,
                        demandMap.get(it.getId())))
                .toList();
    }
}
