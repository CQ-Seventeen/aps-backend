package com.santoni.iot.aps.application.execute.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.execute.dto.OrderStyleProductionDTO;
import com.santoni.iot.aps.application.execute.dto.StyleComponentProductionDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.util.SumKeyUtil;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProduceAssembler {

    public List<StyleComponentProductionDTO> assembleStyleComponentProductionDTOList(List<ProduceOrder> produceOrderList,
                                                                                     Map<Long, List<WeavingPartOrder>> partOrderMap,
                                                                                     List<ProductionSum> productionList,
                                                                                     Map<String, StyleSku> skuMap,
                                                                                     ProduceDate date) {
        var productionMap = productionList.stream()
                .collect(Collectors.toMap(it -> it.getKey().value(),
                        it -> it, (v1, v2) -> v2));
        List<StyleComponentProductionDTO> res = Lists.newArrayList();
        for (var order : produceOrderList) {
            var deliveryDate = TimeUtil.formatYYYYMMDD(order.getDeliveryTime().value());
            var partOrders = partOrderMap.get(order.getId().value());
            if (CollectionUtils.isEmpty(partOrders)) {
                continue;
            }
            for (var partOrder : partOrders) {
                var production = productionMap.get(SumKeyUtil.buildComponentLevelKey(order.getCode(), partOrder.getDemand().getSkuCode(), partOrder.getDemand().getPart()));
                StyleComponentProductionDTO dto = new StyleComponentProductionDTO();
                dto.setProduceOrderCode(order.getCode().value());
                dto.setDeliveryDate(deliveryDate);
                var sku = skuMap.get(partOrder.getDemand().getSkuCode().value());
                if (null != sku) {
                    dto.setStyleCode(sku.getStyleCode().value());
                    dto.setSize(sku.getSize().value());
                }
                dto.setPart(partOrder.getDemand().getPart().value());
                if (null != production) {
                    if (production.getDate().equals(date)) {
                        dto.setProduction(production.getQuantity().getValue());
                    }
                    dto.setTdProduction(production.getTillTodayQuantity().getValue());
                }
                dto.setTotalQuantity(partOrder.getDemand().getQuantity().getValue());
                dto.setLeftQuantity(dto.getTotalQuantity() - dto.getTdProduction());
                res.add(dto);
            }
        }
        return res;
    }

    public List<OrderStyleProductionDTO> assembleOrderStyleProductionDTOList(List<ProduceOrder> produceOrderList,
                                                                             Map<String, StyleSku> skuMap,
                                                                             List<ProductionSum> sumList,
                                                                             Map<String, Map<String, MachineDailyProduction>> dailyRecordMap) {
        Map<String, List<ProductionSum>> productionMap = sumList.stream()
                .collect(Collectors.groupingBy(it -> it.getKey().value()));
        List<OrderStyleProductionDTO> res = Lists.newArrayList();
        for (var produceOrder : produceOrderList) {
            var leftDays = TimeUtil.daysLeft(produceOrder.getDeliveryTime().value());
            var date = TimeUtil.formatYYYYMMDD(produceOrder.getDeliveryTime().value());
            var recordMap = dailyRecordMap.get(produceOrder.getCode().value());
            for (var demand : produceOrder.getDemands()) {
                var dto = new OrderStyleProductionDTO();
                dto.setProduceOrderCode(produceOrder.getCode().value());
                dto.setDeliveryDate(date);
                if (MapUtils.isNotEmpty(recordMap)) {
                    var record = recordMap.get(demand.getSkuCode().value());
                    if (null != record) {
                        dto.setStartDays(TimeUtil.daysBetweenNow(record.getDate().value()));
                    }
                }
                dto.setLeftDays((int) leftDays);
                dto.setTotalQuantity(demand.getOrderQuantity().getValue());
                var productions = productionMap.get(SumKeyUtil.buildSkuLevelKey(produceOrder.getCode(), demand.getSkuCode()));
                if (CollectionUtils.isNotEmpty(productions)) {
                    dto.setTdProduction(productions.stream().mapToInt(it -> it.getQuantity().getValue()).sum());
                }
                dto.setLeftQuantity(dto.getTotalQuantity() - dto.getTdProduction());
                var sku = skuMap.get(demand.getSkuCode().value());
                if (null != sku) {
                    if (dto.getLeftQuantity() > 0) {
                        dto.setLeftMachineDays(sku.calculateMachineDays(Quantity.of(dto.getLeftQuantity())).getDays());
                    }
                    dto.setStyleCode(sku.getStyleCode().value());
                    dto.setSize(sku.getSize().value());
                }
                res.add(dto);
            }
        }
        return res;
    }
}
