package com.santoni.iot.aps.application.order.assembler;

import com.santoni.iot.aps.application.order.dto.WeavingOrderDTO;
import com.santoni.iot.aps.application.order.dto.WeavingPartOrderDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import org.springframework.stereotype.Component;

@Component
public class WeavingOrderDTOAssembler {

    public WeavingOrderDTO convertToWeavingOrderDTO(WeavingOrder weavingOrder, StyleSku sku) {
        var dto = new WeavingOrderDTO();
        dto.setOrderId(weavingOrder.getId().value());
        dto.setOrderCode(weavingOrder.getOrderCode().value());
        dto.setProduceOrderId(weavingOrder.getProduceOrderId().value());
        dto.setProduceOrderCode(weavingOrder.getProduceOrderCode().value());
        if (null != weavingOrder.getDemand().getStyleCode()) {
            dto.setStyleCode(weavingOrder.getDemand().getStyleCode().value());
        }
        if (null != weavingOrder.getDemand().getSize()) {
            dto.setSizeId(weavingOrder.getDemand().getSize().id());
            dto.setSize(weavingOrder.getDemand().getSize().value());
        }
        dto.setSkuCode(weavingOrder.getDemand().getSkuCode().value());
        if (null != weavingOrder.getDemand().getSymbol()) {
            dto.setSymbolId(weavingOrder.getDemand().getSymbol().id());
            dto.setSymbol(weavingOrder.getDemand().getSymbol().value());
        }
        if (null != weavingOrder.getDemand().getColor()) {
            dto.setColorId(weavingOrder.getDemand().getColor().id());
            dto.setColor(weavingOrder.getDemand().getColor().value());
        }
        dto.setQuantity(weavingOrder.getDemand().getWeaveQuantity().getValue());
        dto.setDeliveryTime(TimeUtil.formatGeneralString(weavingOrder.getDeliveryTime().value()));

        if (null != weavingOrder.getPlannedQuantity()) {
            dto.setPlannedQuantity(weavingOrder.getPlannedQuantity().getValue());
            dto.setUnPlannedQuantity(weavingOrder.unPlannedQuantity());
        }
        dto.setStatus(weavingOrder.getStatus().getCode());
        return dto;
    }

    public WeavingPartOrderDTO convertToWeavingPartOrderDTO(WeavingPartOrder partOrder) {
        var dto = new WeavingPartOrderDTO();
        dto.setId(partOrder.getId().value());
        dto.setFactoryId(partOrder.getFactoryId().value());
        dto.setWeavingOrderId(partOrder.getWeavingOrderId().value());
        dto.setProduceOrderId(partOrder.getProduceOrderId().value());
        dto.setProduceOrderCode(partOrder.getProduceOrderCode().value());

        // StylePartDemand fields
        if (null != partOrder.getDemand().getStyleCode()) {
            dto.setStyleCode(partOrder.getDemand().getStyleCode().value());
        }
        if (null != partOrder.getDemand().getSymbol()) {
            dto.setSymbolId(partOrder.getDemand().getSymbol().id());
            dto.setSymbol(partOrder.getDemand().getSymbol().value());
        }
        dto.setSkuCode(partOrder.getDemand().getSkuCode().value());
        if (null != partOrder.getDemand().getSize()) {
            dto.setSizeId(partOrder.getDemand().getSize().id());
            dto.setSize(partOrder.getDemand().getSize().value());
        }
        if (null != partOrder.getDemand().getPart()) {
            dto.setPartId(partOrder.getDemand().getPart().id());
            dto.setPart(partOrder.getDemand().getPart().value());
        }
        if (null != partOrder.getDemand().getColor()) {
            dto.setColorId(partOrder.getDemand().getColor().id());
            dto.setColor(partOrder.getDemand().getColor().value());
        }
        dto.setQuantity(partOrder.getDemand().getQuantity().getValue());

        // WeavingPartOrder fields
        dto.setPlannedQuantity(partOrder.getPlannedQuantity().getValue());
        dto.setDeliveryDate(TimeUtil.formatYYYYMMDD(partOrder.getDeliveryTime().value()));
        if (null != partOrder.getProgram()) {
            dto.setProgram(partOrder.getProgram().name());
        }
        if (null != partOrder.getTaskDetailId()) {
            dto.setTaskDetailId(partOrder.getTaskDetailId().value());
        }
        if (null != partOrder.getFigure()) {
            dto.setFigure(partOrder.getFigure().value());
        }
        if (null != partOrder.getUnit()) {
            dto.setUnit(partOrder.getUnit().value());
        }
        if (null != partOrder.getComment()) {
            dto.setComment(partOrder.getComment().value());
        }
        dto.setStatus(partOrder.getStatus().getCode());
        return dto;
    }
}
