package com.santoni.iot.aps.application.plan.impl;

import com.santoni.iot.aps.application.plan.FactoryPlanOperateApplication;
import com.santoni.iot.aps.application.plan.command.AssignOrderToFactoryCommand;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.order.service.OrderDomainService;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryPlan;
import com.santoni.iot.aps.domain.plan.entity.valueobj.context.AssignOrderToFactoryContext;
import com.santoni.iot.aps.domain.plan.repository.FactoryPlanRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import com.santoni.iot.aps.domain.support.repository.OrganizationRepository;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FactoryPlanOperateApplicationImpl implements FactoryPlanOperateApplication {

    @Autowired
    private OrderDomainService orderDomainService;

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private FactoryPlanRepository factoryPlanRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void assignOrderToFactory(AssignOrderToFactoryCommand command) {
        var context = prepareAssignContext(command);
        var task = context.factoryPlan().arrangeProduceOrder(context.demand());
        transactionTemplate.execute(status -> {
            try {
                var taskId = factoryPlanRepository.saveTask(task);
                factoryPlanRepository.saveTaskDetail(taskId, task.getAssignDetail());
                generateWeaveOrder(context);
                return true;
            } catch (Exception e) {
                log.error("Save FactoryPlanTask error, task:{}", JacksonUtil.toJson(task), e);
                status.setRollbackOnly();
                return false;
            }
        });
    }

    private AssignOrderToFactoryContext prepareAssignContext(AssignOrderToFactoryCommand command) {
        var produceOrder = produceOrderRepository.produceOrderDetailById(new ProduceOrderId(command.getProduceOrderId()));
        if (null == produceOrder) {
            throw new IllegalArgumentException("生产单不存在:" + command.getProduceOrderId());
        }
        var factory = organizationRepository.getFactoryById(new FactoryId(command.getFactoryId()));
        if (null == factory) {
            throw new IllegalArgumentException("工厂不存在:" + command.getFactoryId());
        }
        var skuMap = getStyleSkuMap(produceOrder);
        var demand = orderDomainService.calculateProduceDemand(produceOrder, skuMap);
        var factoryTaskList = factoryPlanRepository.findByFactoryId(factory.getId(),
                new StartTime(LocalDateTime.now()), new EndTime(produceOrder.getDeliveryTime().value()));
        var machineMap = machineRepository.listMachineByFactory(factory.getId())
                .stream().collect(Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value()));
        var factoryPlan = FactoryPlan.of(factory.getId(), machineMap, factoryTaskList);
        return new AssignOrderToFactoryContext(factoryPlan, demand, skuMap);
    }

    private Map<String, StyleSku> getStyleSkuMap(ProduceOrder order) {
        var skuCodeList = order.getDemands().stream().map(StyleDemand::getSkuCode).toList();
        return styleRepository.listStyleSkuByCode(null, skuCodeList, true)
                .stream()
                .collect(Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v2));
    }

    private void generateWeaveOrder(AssignOrderToFactoryContext context) {
        var weavingOrders = context.demand().getProduceOrder().generateWeavingOrders(codeRepository, context.factoryPlan().getFactoryId());
        for (var weavingOrder : weavingOrders ) {
            var weavingOrderId = weavingOrderRepository.saveWeavingOrder(weavingOrder);
            var weavingPartOrders = weavingOrder.derivePartOrders(context.skuMap().get(weavingOrder.getDemand().getSkuCode().value()), weavingOrderId,
                    null, null, null, null, null);
            weavingOrderRepository.batchSavePartOrder(weavingPartOrders);
        }
    }

}
