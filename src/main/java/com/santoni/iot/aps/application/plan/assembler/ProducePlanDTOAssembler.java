package com.santoni.iot.aps.application.plan.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.plan.dto.order.ProduceLevelPlanDTO;
import com.santoni.iot.aps.application.plan.dto.order.WeavingPartPlanDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ProducePlanDTOAssembler {
    public ProduceLevelPlanDTO assembleProducePlanDTO(ProduceOrder produceOrder,
                                                      Map<Long, List<WeavingPartOrder>> partOrderMap,
                                                      Map<String, Map<String, Map<String, StyleComponent>>> componentMap) {
        var dto = new ProduceLevelPlanDTO();
        dto.setProduceOrderId(produceOrder.getId().value());
        dto.setProduceOrderCode(produceOrder.getCode().value());
        dto.setTotalQuantity(produceOrder.getTotalQuantity());
        dto.setDeliveryTime(TimeUtil.formatYYYYMMDD(produceOrder.getDeliveryTime().value()));
        var partOrders = partOrderMap.get(produceOrder.getId().value());
        int plannedQuantity = 0;
        var orderComponents = componentMap.getOrDefault(produceOrder.getCode().value(), Collections.emptyMap());
        if (CollectionUtils.isNotEmpty(partOrders)) {
            List<WeavingPartPlanDTO> weavingPlanList = Lists.newArrayList();
            for (var partOrder : partOrders) {
                var weavingDTO = new WeavingPartPlanDTO();
                if (null != partOrder.getWeavingOrderId()) {
                    weavingDTO.setWeavingOrderId(partOrder.getWeavingOrderId().value());
                }
                weavingDTO.setWeavingPartOrderId(partOrder.getId().value());
                if (null != partOrder.getDemand().getStyleCode()) {
                    weavingDTO.setStyleCode(partOrder.getDemand().getStyleCode().value());
                }
                if (null != partOrder.getDemand().getPart()) {
                    weavingDTO.setPart(partOrder.getDemand().getPart().value());
                }
                if (null != partOrder.getDemand().getColor()) {
                    weavingDTO.setColor(partOrder.getDemand().getColor().value());
                }
                weavingDTO.setQuantity(partOrder.getDemand().getQuantity().getValue());
                if (null != partOrder.getPlannedQuantity()) {
                    weavingDTO.setPlannedQuantity(partOrder.getPlannedQuantity().getValue());
                    weavingDTO.setUnPlannedQuantity(partOrder.unPlannedQuantity());
                }
                var component = orderComponents.getOrDefault(partOrder.getDemand().getSkuCode().value(), Map.of())
                        .get(partOrder.getDemand().getPart().value());
                if (null != component) {
                    weavingDTO.setCylinderDiameter(component.getMachineSize().getCylinderDiameter().value());
                    weavingDTO.setNeedleSpacing(component.getMachineSize().getNeedleSpacing().value());
                    if (component.isPrincipal()) {
                        plannedQuantity += partOrder.getPlannedQuantity().getValue();
                    }
                }
                weavingPlanList.add(weavingDTO);
            }
            dto.setWeavingPlanList(weavingPlanList);
        }
        dto.setPlannedQuantity(plannedQuantity);
        return dto;
    }

}
