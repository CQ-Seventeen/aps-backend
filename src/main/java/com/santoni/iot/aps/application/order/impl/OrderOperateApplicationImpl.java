package com.santoni.iot.aps.application.order.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.order.OrderOperateApplication;
import com.santoni.iot.aps.application.order.assembler.OrderAssembler;
import com.santoni.iot.aps.application.order.command.*;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.context.BatchOperateProduceOrderContext;
import com.santoni.iot.aps.domain.order.entity.valueobj.context.OperateProduceOrderContext;
import com.santoni.iot.aps.domain.order.entity.valueobj.context.OperateWeavingOrderContext;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.order.service.OrderDomainService;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import com.santoni.iot.aps.domain.support.repository.CustomerRepository;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderOperateApplicationImpl implements OrderOperateApplication {

    @Autowired
    private OrderDomainService orderDomainService;

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderAssembler orderAssembler;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void createWeavingOrder(CreateWeavingOrderCommand cmd) {
        var context = prepareOperateWeavingOrderContext(cmd);
        var code = getWeavingOrderCode(cmd);
        var weavingOrder = orderAssembler.composeWeavingOrderFromCreateCmd(cmd, code, context.produceOrder(), context.skuCode());
        weavingOrderRepository.saveWeavingOrder(weavingOrder);
    }

    @Override
    public void batchCreateWeavingOrder(BatchCreateWeavingOrderCommand cmd) {
    }

    @Override
    public void updateWeavingOrder(UpdateWeavingOrderCommand cmd) {
        var existOrder = weavingOrderRepository.weavingOrderDetailById(new WeavingOrderId(cmd.getOrderId()));
        if (null == existOrder) {
            throw new IllegalArgumentException("织造单不存在");
        }
        var context = prepareOperateWeavingOrderContext(cmd);
        var weavingOrder = orderAssembler.composeWeavingOrderFromUpdateCmd(cmd, existOrder, context.produceOrder(), context.skuCode());
        if (!orderDomainService.canWeavingOrderModify(existOrder, weavingOrder)) {
            throw new IllegalArgumentException("织造单不允许修改");
        }
        weavingOrderRepository.saveWeavingOrder(weavingOrder);
    }

    @Override
    public void createProduceOrder(CreateProduceOrderCommand cmd) {
        var context = prepareOperateProduceOrderContext(cmd);
        var code = getProduceOrderCode(cmd);

        var produceOrder = orderAssembler.composeProduceOrderFromCreateCmd(cmd, code, context.skuCodeMap(), context.customer());
        transactionTemplate.execute(status -> {
            try {
                produceOrderRepository.saveProduceOrder(produceOrder);
                return 1;
            } catch (Exception e) {
                log.error("create ProduceOrder transaction error", e);
                status.setRollbackOnly();
                return -1;
            }
        });
    }

    @Override
    public BatchOperateResultDTO batchCreateProduceOrder(BatchCreateProduceOrderCommand cmd) {
        var context = prepareBatchOperateProduceOrderContext(cmd);
        var codeQueue = getProduceOrderCodeQueue(cmd);

        return batchSaveProduceOrder(codeQueue, context, cmd);
    }

    @Override
    public void updateProduceOrder(UpdateProduceOrderCommand cmd) {
        var exist = produceOrderRepository.produceOrderDetailById(new ProduceOrderId(cmd.getOrderId()));
        if (null == exist) {
            throw new IllegalArgumentException("生产单不存在,id:" + cmd.getOrderId());
        }
        var context = prepareOperateProduceOrderContext(cmd);
        var produceOrder = orderAssembler.composeProduceOrderFromUpdateCmd(cmd, exist, context.skuCodeMap(), context.customer());
        if (!orderDomainService.canProduceOrderModify(exist, produceOrder)) {
            throw new IllegalArgumentException("生产单不允许修改");
        }
        transactionTemplate.execute(status -> {
            try {
                produceOrderRepository.saveProduceOrder(produceOrder);
                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("update ProduceOrder transaction error", e);
                return -1;
            }
        });
    }

    @Override
    public void importOuterProduceOrder(OperateOuterProduceOrderCommand cmd) {
        var order = orderAssembler.composeProduceOrderFromOuterCmd(cmd);
        var exist = produceOrderRepository.produceOrderDetailByCode(order.getCode());
        transactionTemplate.execute(status -> {
            try {
                if (null == exist) {
                    var orderId = produceOrderRepository.saveProduceOrder(order);
                    var partOrders = cmd.getPartOrders().stream()
                            .map(it -> orderAssembler.composeWeavingPartOrderFromOperateCmd(it, orderId,
                                    order.getCode(), order.getDeliveryTime())).toList();
                    weavingOrderRepository.batchSavePartOrder(partOrders);
                } else {
                    exist.applyChange(order);
                    produceOrderRepository.saveProduceOrder(exist);
                    var existPartOrderMap = weavingOrderRepository.listPartOrderByProduceOrderIds(Lists.newArrayList(exist.getId()))
                            .stream().collect(Collectors.toMap(it -> it.getDemand().getUniqueKey(), it -> it));
                    var newPartOrders = cmd.getPartOrders().stream()
                            .map(it -> orderAssembler.composeWeavingPartOrderFromOperateCmd(it, exist.getId(),
                                    order.getCode(), order.getDeliveryTime())).toList();
                    List<WeavingPartOrder> insertOrders = Lists.newArrayList();
                    List<WeavingPartOrder> updateOrders = Lists.newArrayList();

                    for (var partOrder : newPartOrders) {
                        var existPartOrder = existPartOrderMap.get(partOrder.getDemand().getUniqueKey());
                        if (null == existPartOrder) {
                            insertOrders.add(partOrder);
                        } else {
                            existPartOrder.applyChange(partOrder);
                            updateOrders.add(existPartOrder);
                            existPartOrderMap.remove(existPartOrder.getDemand().getUniqueKey());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(insertOrders)) {
                        weavingOrderRepository.batchSavePartOrder(insertOrders);
                    }
                    if (CollectionUtils.isNotEmpty(updateOrders)) {
                        for (var partOrder : updateOrders) {
                            weavingOrderRepository.updatePartOrder(partOrder);
                        }
                    }

                }
                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("update ProduceOrder transaction error", e);
                return -1;
            }
        });

    }

    private OperateWeavingOrderContext prepareOperateWeavingOrderContext(CreateWeavingOrderCommand cmd) {
        var produceOrder = produceOrderRepository.produceOrderDetailById(new ProduceOrderId(cmd.getProduceOrderId()));
        if (null == produceOrder) {
            throw new IllegalArgumentException("生产单不存在,id:" + cmd.getProduceOrderId());
        }
        var style = styleRepository.getStyleSkuByCode(produceOrder.getCode(), new SkuCode(cmd.getSkuCode()), false);
        if (null == style) {
            throw new IllegalArgumentException("款式不存在,code:" + cmd.getSkuCode());
        }
        return new OperateWeavingOrderContext(produceOrder, style.getCode());
    }

    private WeavingOrderCode getWeavingOrderCode(CreateWeavingOrderCommand cmd) {
        WeavingOrderCode orderCode;
        if (StringUtils.isBlank(cmd.getCode())) {
            orderCode = codeRepository.getWeavingOrderCode();
        } else {
            orderCode = new WeavingOrderCode(cmd.getCode());
        }
        var existOrder = weavingOrderRepository.weavingOrderDetailByCode(orderCode);
        if (null != existOrder) {
            throw new IllegalArgumentException("织造订单编号已存在,code:" + cmd.getCode());
        }
        return orderCode;
    }

    private BatchOperateProduceOrderContext prepareBatchOperateProduceOrderContext(BatchCreateProduceOrderCommand cmd) {
        Set<SkuCode> skuCodeSet = Sets.newHashSet();
        Map<String, Set<String>> skuCodeOrderMap = Maps.newHashMap();
        Set<CustomerCode> customerCodeSet = Sets.newHashSet();

        for (var order : cmd.getOrderList()) {
            customerCodeSet.add(new CustomerCode(order.getCustomerCode()));
            for (var demand : order.getStyleDemands()) {
                skuCodeSet.add(new SkuCode(demand.getSkuCode()));
                var existEntry = skuCodeOrderMap.get(demand.getSkuCode());
                if (CollectionUtils.isEmpty(existEntry)) {
                    skuCodeOrderMap.put(demand.getSkuCode(), Sets.newHashSet(order.getCode()));
                } else {
                    existEntry.add(order.getCode());
                }
            }
        }

        var skuCodeMap = styleRepository.listStyleSkuByCode(null, skuCodeSet, false)
                .stream()
                .collect(Collectors.toMap(it -> it.getCode().value(), StyleSku::getCode, (v1, v2) -> v1));
        if (skuCodeMap.size() != skuCodeSet.size()) {
            throw new IllegalArgumentException("有不存在的款式");
        }

        var customerMap = customerRepository.listCustomerByCode(customerCodeSet).
                stream()
                .collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v1));

        if (customerMap.size() != customerCodeSet.size()) {
            throw new IllegalArgumentException("有不存在的客户");
        }
        return new BatchOperateProduceOrderContext(customerMap, skuCodeMap);
    }

    private OperateProduceOrderContext prepareOperateProduceOrderContext(CreateProduceOrderCommand cmd) {
        var skuCodeList = cmd.getStyleDemands().stream().map(it -> new SkuCode(it.getSkuCode())).distinct().toList();
        var skuCodeMap = styleRepository.listStyleSkuByCode(null, skuCodeList, false)
                .stream()
                .collect(Collectors.toMap(it -> it.getCode().value(), StyleSku::getCode, (v1, v2) -> v1));
        if (skuCodeMap.size() != skuCodeList.size()) {
            throw new IllegalArgumentException("款式编号不存在");
        }
        var customer = customerRepository.getCustomerByCode(new CustomerCode(cmd.getCustomerCode()));
        if (null == customer) {
            throw new IllegalArgumentException("客户不存在");
        }
        return new OperateProduceOrderContext(customer, skuCodeMap);
    }

    private ProduceOrderCode getProduceOrderCode(CreateProduceOrderCommand cmd) {
        ProduceOrderCode orderCode;
        if (StringUtils.isBlank(cmd.getCode())) {
            orderCode = codeRepository.getProduceOrderCode();
        } else {
            orderCode = new ProduceOrderCode(cmd.getCode());
        }
        var existOrder = produceOrderRepository.produceOrderDetailByCode(orderCode);
        if (null != existOrder) {
            throw new IllegalArgumentException("生产订单编号已存在,code:" + cmd.getCode());
        }
        return orderCode;
    }

    private Deque<ProduceOrderCode> getProduceOrderCodeQueue(BatchCreateProduceOrderCommand cmd) {
        List<ProduceOrderCode> nonBlankCode = Lists.newArrayList();
        int count = 0;
        for (var order : cmd.getOrderList()) {
            if (StringUtils.isBlank(order.getCode())) {
                count++;
            } else {
                nonBlankCode.add(new ProduceOrderCode(order.getCode()));
            }
        }
        var newCodeList = codeRepository.getMultiProduceOrderCode(count);
        nonBlankCode.addAll(newCodeList);
        checkProduceCodeListExist(nonBlankCode);

        return Lists.newLinkedList(newCodeList);
    }

    private void checkProduceCodeListExist(List<ProduceOrderCode> orderCodeList) {
        var exist = produceOrderRepository.listProduceOrderByCode(orderCodeList);
        if (!exist.isEmpty()) {
            throw new IllegalArgumentException("生产单已存在,编号:" +
                    exist.stream().map(it -> it.getCode().value()).collect(Collectors.joining(",")));
        }
    }

    private BatchOperateResultDTO batchSaveProduceOrder(Deque<ProduceOrderCode> codeQueue,
                                                        BatchOperateProduceOrderContext context,
                                                        BatchCreateProduceOrderCommand cmd) {
        List<String> failCodeList = Lists.newArrayList();
        int success = 0, fail = 0;
        for (var orderCmd : cmd.getOrderList()) {
            var orderCode = StringUtils.isBlank(orderCmd.getCode()) ? codeQueue.pop() : new ProduceOrderCode(orderCmd.getCode());

            var customer = context.customerMap().get(orderCmd.getCustomerCode());
            var produceOrder = orderAssembler.composeProduceOrderFromCreateCmd(orderCmd, orderCode, context.skuCodeMap(), customer);

            boolean res = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                try {
                    produceOrderRepository.saveProduceOrder(produceOrder);
                    return true;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return false;
                }
            }));
            if (res) {
                success++;
            } else {
                failCodeList.add(orderCode.value());
                fail++;
            }
        }
        return new BatchOperateResultDTO(success, fail, failCodeList);
    }
}
