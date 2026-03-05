package com.santoni.iot.aps.infrastructure.factory;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.bom.entity.valueobj.ProgramFile;
import com.santoni.iot.aps.domain.order.entity.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.OuterFactory;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerName;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderDemandPO;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderPO;
import com.santoni.iot.aps.infrastructure.po.WeavingOrderPO;
import com.santoni.iot.aps.infrastructure.po.WeavingPartOrderPO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderFactory {

    public ProduceOrderPO convertProduceOrderEntityToPO(ProduceOrder produceOrder) {
        var po = new ProduceOrderPO();
        if (null != produceOrder.getOuterId()) {
            po.setOuterId(produceOrder.getOuterId().value());
        }
        if (null != produceOrder.getFactory()) {
            po.setOrgId(produceOrder.getFactory().id());
            po.setOrgName(produceOrder.getFactory().code());
        }
        po.setCode(produceOrder.getCode().value());
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != produceOrder.getCustomer()) {
            po.setCustomerCode(produceOrder.getCustomer().getCode().value());
            po.setCustomerName(produceOrder.getCustomer().getName().value());
        }
        po.setDeliveryTime(produceOrder.getDeliveryTime().value());
        if (null != produceOrder.getManufactureBatch()) {
            po.setManufactureBatch(produceOrder.getManufactureBatch().value());
        }
        if (null != produceOrder.getManufactureDate()) {
            po.setManufactureDate(produceOrder.getManufactureDate().value());
        }
        po.setStatus(produceOrder.getStatus().getCode());
        return po;
    }

    public List<ProduceOrderDemandPO> convertProduceOrderDemandEntityToPO(List<StyleDemand> demandList, long produceOrderId) {
        List<ProduceOrderDemandPO> poList = Lists.newArrayListWithExpectedSize(demandList.size());
        for (StyleDemand demand : demandList) {
            var demandPO = new ProduceOrderDemandPO();
            demandPO.setProduceOrderId(produceOrderId);
            if (null != demand.getStyleCode()) {
                demandPO.setStyleCode(demand.getStyleCode().value());
            }
            demandPO.setOrderQuantity(demand.getOrderQuantity() != null ? demand.getOrderQuantity().getValue() : 0);
            demandPO.setWeaveQuantity(demand.getWeaveQuantity().getValue());
            demandPO.setSampleQuantity(demand.getSampleQuantity() != null ? demand.getSampleQuantity().getValue() : 0);
            
            if (null != demand.getSkuCode()) {
                demandPO.setSkuCode(demand.getSkuCode().value());
            }
            if (null != demand.getSize()) {
                demandPO.setSizeId(demand.getSize().id());
                demandPO.setSize(demand.getSize().value());
            }
            if (null != demand.getSymbol()) {
                demandPO.setSymbolId(demand.getSymbol().id());
                demandPO.setSymbol(demand.getSymbol().value());
            }
            if (null != demand.getColor()) {
                demandPO.setColorId(demand.getColor().id());
                demandPO.setColor(demand.getColor().value());
            }
            poList.add(demandPO);
        }
        return poList;
    }

    public ProduceOrder composeProduceOrderEntityFromPO(ProduceOrderPO orderPO,
                                                        List<ProduceOrderDemandPO> demandPOList) {
        List<StyleDemand> styleDemandList = CollectionUtils.isEmpty(demandPOList) ? List.of() :
                demandPOList.stream().map(this::composeStyleDemandFromPO).toList();
        
        return new ProduceOrder(
                new ProduceOrderId(orderPO.getId()),
                StringUtils.isNotBlank(orderPO.getOuterId()) ? new OuterOrderId(orderPO.getOuterId()) : null,
                new ProduceOrderCode(orderPO.getCode()),
                StringUtils.isNotBlank(orderPO.getOrgId()) ? new OuterFactory(orderPO.getOrgId(), orderPO.getOrgName()) : null,
                StringUtils.isNotBlank(orderPO.getCustomerCode()) ? Customer.newOf(new CustomerCode(orderPO.getCustomerCode()),
                        new CustomerName(orderPO.getCustomerName())) : null,
                styleDemandList,
                new DeliveryTime(orderPO.getDeliveryTime()),
                StringUtils.isNotBlank(orderPO.getManufactureBatch()) ? new ManufactureBatch(orderPO.getManufactureBatch()) : null,
                null != orderPO.getManufactureDate() ? new ManufactureDate(orderPO.getManufactureDate()) : null,
                ProduceOrderStatus.findByCode(orderPO.getStatus())
        );
    }

    private StyleDemand composeStyleDemandFromPO(ProduceOrderDemandPO po) {
        return StyleDemand.of(
                new SkuCode(po.getSkuCode()),
                StringUtils.isNotBlank(po.getSymbol()) ? new Symbol(po.getSymbolId(), po.getSymbol()) : null,
                StringUtils.isNotBlank(po.getStyleCode()) ? new StyleCode(po.getStyleCode()) : null,
                StringUtils.isNotBlank(po.getSize()) ? new Size(po.getSizeId(), po.getSize()) : null,
                StringUtils.isNotBlank(po.getColor()) ? new Color(po.getColorId(), po.getColor()) : null,
                Quantity.of(po.getOrderQuantity()),
                Quantity.of(po.getWeaveQuantity()),
                Quantity.of(po.getSampleQuantity()),
                null
        );
    }

    public WeavingOrderPO convertWeavingOrderEntityToPO(WeavingOrder weavingOrder) {
        var po = new WeavingOrderPO();
        po.setInstituteId(PlanContext.getInstituteId());
        po.setFactoryId(weavingOrder.getFactoryId().value());
        po.setCode(weavingOrder.getOrderCode().value());
        po.setProduceOrderId(weavingOrder.getProduceOrderId().value());
        po.setProduceOrderCode(weavingOrder.getProduceOrderCode().value());
        if (null != weavingOrder.getDemand().getStyleCode()) {
            po.setStyleCode(weavingOrder.getDemand().getStyleCode().value());
        }
        if (null != weavingOrder.getDemand().getSize()) {
            po.setSizeId(weavingOrder.getDemand().getSize().id());
            po.setSize(weavingOrder.getDemand().getSize().value());
        }
        po.setSkuCode(weavingOrder.getDemand().getSkuCode().value());
        if (null != weavingOrder.getDemand().getSymbol()) {
            po.setSymbolId(weavingOrder.getDemand().getSymbol().id());
            po.setSymbol(weavingOrder.getDemand().getSymbol().value());
        }
        if (null != weavingOrder.getDemand().getColor()) {
            po.setColorId(weavingOrder.getDemand().getColor().id());
            po.setColor(weavingOrder.getDemand().getColor().value());
        }
        po.setQuantity(weavingOrder.getDemand().getWeaveQuantity().getValue());
        po.setPlannedQuantity(weavingOrder.getPlannedQuantity().getValue());
        po.setFinishTime(weavingOrder.getDeliveryTime().value());
        po.setStatus(weavingOrder.getStatus().getCode());
        return po;
    }

    public WeavingOrder composeWeavingOrder(WeavingOrderPO po) {
        return new WeavingOrder(
                new WeavingOrderId(po.getId()),
                new FactoryId(po.getFactoryId()),
                new WeavingOrderCode(po.getCode()),
                new ProduceOrderId(po.getProduceOrderId()),
                new ProduceOrderCode(po.getProduceOrderCode()),
                composeStyleDemandFromOrderPO(po),
                Quantity.of(po.getPlannedQuantity()),
                new DeliveryTime(po.getFinishTime()),
                WeavingOrderStatus.findByCode(po.getStatus())
        );
    }

    private StyleDemand composeStyleDemandFromOrderPO(WeavingOrderPO po) {
        return StyleDemand.of(
                new SkuCode(po.getSkuCode()),
                StringUtils.isNotBlank(po.getSymbol()) ? new Symbol(po.getSymbolId(), po.getSymbol()) : null,
                StringUtils.isNotBlank(po.getStyleCode()) ? new StyleCode(po.getStyleCode()) : null,
                StringUtils.isNotBlank(po.getSize()) ? new Size(po.getSizeId(), po.getSize()) : null,
                StringUtils.isNotBlank(po.getColor()) ? new Color(po.getColorId(), po.getColor()) : null,
                Quantity.of(po.getQuantity()),
                new DeliveryTime(po.getFinishTime())
        );
    }

    private StylePartDemand composeStylePartDemandFromOrderPO(WeavingPartOrderPO po) {
        return new StylePartDemand(
                new SkuCode(po.getSkuCode()),
                StringUtils.isNotBlank(po.getStyleCode()) ? new StyleCode(po.getStyleCode()) : null,
                StringUtils.isNotBlank(po.getSymbol()) ? new Symbol(po.getSymbolId(), po.getSymbol()) : null,
                StringUtils.isNotBlank(po.getSize()) ? new Size(po.getSizeId(), po.getSize()) : null,
                StringUtils.isNotBlank(po.getPart()) ? new Part(po.getPartId(), po.getPart()) : null,
                StringUtils.isNotBlank(po.getColor()) ? new Color(po.getColorId(), po.getColor()) : null,
                Quantity.of(po.getQuantity())
        );
    }

    public WeavingPartOrder composeWeavingPartOrder(WeavingPartOrderPO po) {
        return new WeavingPartOrder(new WeavingPartOrderId(po.getId()),
                null == po.getFactoryId() ? null : new FactoryId(po.getFactoryId()),
                null == po.getWeavingOrderId() ? null : new WeavingOrderId(po.getWeavingOrderId()),
                new ProduceOrderId(po.getProduceOrderId()),
                new ProduceOrderCode(po.getProduceOrderCode()),
                composeStylePartDemandFromOrderPO(po),
                Collections.emptyList(),
                Quantity.of(po.getPlannedQuantity()),
                new DeliveryTime(po.getFinishTime()),
                StringUtils.isNotBlank(po.getProgram()) ? new ProgramFile(po.getProgram(), null) : null,
                StringUtils.isNotBlank(po.getTaskDetailId()) ? new TaskDetailId(po.getTaskDetailId()) : null,
                StringUtils.isNotBlank(po.getFigure()) ? new Figure(po.getFigure()) : null,
                StringUtils.isNotBlank(po.getUnit()) ? new Unit(po.getUnit()) : null,
                null != po.getComment() ? new OrderComment(po.getComment()) : null,
                WeavingOrderStatus.findByCode(po.getStatus())
        );
    }

    public WeavingPartOrderPO convertWeavingPartOrderEntityToPO(WeavingPartOrder order) {
        var po = new WeavingPartOrderPO();
        if (null != order.getId()) {
            po.setId(order.getId().value());
        }
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != order.getFactoryId()) {
            po.setFactoryId(order.getFactoryId().value());
        }
        if (null != order.getWeavingOrderId()) {
            po.setWeavingOrderId(order.getWeavingOrderId().value());
        }
        po.setProduceOrderId(order.getProduceOrderId().value());
        po.setProduceOrderCode(order.getProduceOrderCode().value());
        if (null != order.getDemand().getStyleCode()) {
            po.setStyleCode(order.getDemand().getStyleCode().value());
        }
        po.setSkuCode(order.getDemand().getSkuCode().value());
        if (null != order.getDemand().getSize()) {
            po.setSizeId(order.getDemand().getSize().id());
            po.setSize(order.getDemand().getSize().value());
        }
        if (null != order.getDemand().getPart()) {
            po.setPartId(order.getDemand().getPart().id());
            po.setPart(order.getDemand().getPart().value());
        }
        if (null != order.getDemand().getColor()) {
            po.setColorId(order.getDemand().getColor().id());
            po.setColor(order.getDemand().getColor().value());
        }
        if (null != order.getProgram()) {
            po.setProgram(order.getProgram().name());
        }
        if (null != order.getTaskDetailId()) {
            po.setTaskDetailId(order.getTaskDetailId().value());
        }
        if (null != order.getFigure()) {
            po.setFigure(order.getFigure().value());
        }
        if (null != order.getUnit()) {
            po.setUnit(order.getUnit().value());
        }
        if (null != order.getComment()) {
            po.setComment(order.getComment().value());
        }
        po.setQuantity(order.getDemand().getQuantity().getValue());
        po.setPlannedQuantity(order.getPlannedQuantity().getValue());
        po.setFinishTime(order.getDeliveryTime().value());
        po.setStatus(order.getStatus().getCode());
        return po;
    }
}
