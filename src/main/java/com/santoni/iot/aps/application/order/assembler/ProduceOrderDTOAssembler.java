package com.santoni.iot.aps.application.order.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.order.context.BuildProduceDemandContext;
import com.santoni.iot.aps.application.order.context.BuildProduceOrderContext;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.order.dto.StyleDemandDTO;
import com.santoni.iot.aps.application.plan.dto.factory.OrderDemandOfMachineDTO;
import com.santoni.iot.aps.application.plan.dto.factory.ProduceOrderDemandDTO;
import com.santoni.iot.aps.application.plan.dto.factory.StylePartDemandDTO;
import com.santoni.iot.aps.application.support.assembler.OrganizationAssembler;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProduceOrderDTOAssembler {

    @Autowired
    private OrganizationAssembler organizationAssembler;

    public ProduceOrderDTO convertToProduceOrderDTO(ProduceOrder produceOrder, BuildProduceOrderContext context) {
        var dto = new ProduceOrderDTO();
        dto.setOrderId(produceOrder.getId().value());
        dto.setOrderCode(produceOrder.getCode().value());
        if (null != produceOrder.getCustomer()) {
            dto.setCustomerCode(produceOrder.getCustomer().getCode().value());
            dto.setCustomerName(produceOrder.getCustomer().getName().value());
        }
        dto.setDemands(produceOrder.getDemands()
                .stream()
                .map(it -> convertToStyleDemandDTO(it, null == context ? Collections.emptyMap() : context.styleSkuMap())).toList());
        dto.setDeliveryTime(TimeUtil.formatGeneralString(produceOrder.getDeliveryTime().value()));
        if (null != produceOrder.getManufactureBatch()) {
            dto.setManufactureBatch(produceOrder.getManufactureBatch().value());
        }
        if (null != produceOrder.getManufactureDate()) {
            dto.setManufactureDate(TimeUtil.formatGeneralString(produceOrder.getManufactureDate().value()));
        }
        if (null != context) {
            var task = context.taskMap().get(produceOrder.getId().value());
            if (null != task) {
                var factory = context.factoryMap().get(task.getFactoryId().value());
                if (null != factory) {
                    dto.setAssignedFactory(organizationAssembler.convertToFactoryDTO(factory));
                }
            }
        }
        dto.setStatus(produceOrder.getStatus().getCode());
        return dto;
    }

    private StyleDemandDTO convertToStyleDemandDTO(StyleDemand styleDemand, Map<String, StyleSku> styleSkuMap) {
        var dto = new StyleDemandDTO();
        dto.setSkuCode(styleDemand.getSkuCode().value());
        if (null != styleDemand.getSymbol()) {
            dto.setSymbolId(styleDemand.getSymbol().id());
            dto.setSymbol(styleDemand.getSymbol().value());
        }
        if (null != styleDemand.getStyleCode()) {
            dto.setStyleCode(styleDemand.getStyleCode().value());
        }
        if (null != styleDemand.getSize()) {
            dto.setSizeId(styleDemand.getSize().id());
            dto.setSize(styleDemand.getSize().value());
        }
        if (null != styleDemand.getColor()) {
            dto.setColorId(styleDemand.getColor().id());
            dto.setColor(styleDemand.getColor().value());
        }
        dto.setOrderQuantity(styleDemand.getOrderQuantity() != null ? styleDemand.getOrderQuantity().getValue() : 0);
        dto.setWeaveQuantity(styleDemand.getWeaveQuantity().getValue());
        dto.setSampleQuantity(styleDemand.getSampleQuantity() != null ? styleDemand.getSampleQuantity().getValue() : 0);
        var sku = styleSkuMap.get(styleDemand.getSkuCode().value());
        if (null != sku) {
            var days = sku.calculateMachineDays(styleDemand.getWeaveQuantity());
            dto.setExpectedDays(days.getDays());
        }
        return dto;
    }

    public ProduceOrderDemandDTO assembleOrderDemandDTO(ProduceOrderCode orderCode, BuildProduceDemandContext context) {
        List<OrderDemandOfMachineDTO> demandDTOList = Lists.newArrayListWithExpectedSize(context.componentMap().size());
        for (var cylinder : context.cylinderList()) {
            var componentList = context.componentMap().get(cylinder);
            demandDTOList.add(assembleOrderDemandOfMachineDTO(context.demandMap(), context.skuMap(), componentList, cylinder));
        }
        var dto = new ProduceOrderDemandDTO();
        dto.setDemandOfMachine(demandDTOList);
        dto.setOrderCode(orderCode.value());
        return dto;
    }

    private OrderDemandOfMachineDTO assembleOrderDemandOfMachineDTO(Map<String, List<StyleDemand>> demandMap,
                                                                    Map<String, StyleSku> skuMap,
                                                                    List<StyleComponent> componentList,
                                                                    int cylinderDiameter) {
        var componentMap = componentList.stream().collect(Collectors.groupingBy(it -> it.getSkuCode().value()));
        double totalSeconds = 0;
        List<StylePartDemandDTO> demandDTOList = Lists.newArrayListWithExpectedSize(componentMap.size());
        for (var entry : componentMap.entrySet()) {
            var demandList = demandMap.get(entry.getKey());
            if (CollectionUtils.isEmpty(demandList)) {
                continue;
            }
            var sku = skuMap.get(entry.getKey());
            if (null == sku) {
                continue;
            }
            var dto = new StylePartDemandDTO();
            dto.setStyleCode(sku.getStyleCode().value());
            dto.setSize(sku.getSize().value());
            dto.setQuantity(demandList.stream().mapToInt(it -> it.getWeaveQuantity().getValue()).sum());
            List<String> partList = Lists.newArrayListWithExpectedSize(entry.getValue().size());
            for (var part : entry.getValue()) {
                partList.add(part.getPart().value());
                totalSeconds += (part.getNumber().value() * dto.getQuantity() / (double) part.getRatio().value()) * part.getExpectedProduceTime().value();
            }
            dto.setPart(partList);
            demandDTOList.add(dto);
        }
        var dto = new OrderDemandOfMachineDTO();
        dto.setDemands(demandDTOList);
        dto.setCylinderDiameter(cylinderDiameter);
        dto.setTotalDays(Math.ceil(10 * totalSeconds / PlanConstant.SECONDS_PER_DAY) / 10);
        return dto;
    }
}
