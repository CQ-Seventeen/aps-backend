package com.santoni.iot.aps.application.order.assembler;

import com.santoni.iot.aps.application.order.command.*;
import com.santoni.iot.aps.application.order.query.PageWeavingOrderQuery;
import com.santoni.iot.aps.application.order.query.ProduceOrderQuery;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.ProgramFile;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.support.entity.organization.OuterFactory;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.StylePartDemand;
import com.santoni.iot.aps.domain.order.entity.WeavingOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.Search;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class OrderAssembler {

    public WeavingOrder composeWeavingOrderFromCreateCmd(CreateWeavingOrderCommand cmd,
                                                         WeavingOrderCode orderCode,
                                                         ProduceOrder produceOrder,
                                                         SkuCode skuCode) {
        return WeavingOrder.newOf(new FactoryId(cmd.getFactoryId()),
                orderCode,
                produceOrder.getId(),
                produceOrder.getCode(),
                StyleDemand.of(skuCode, Quantity.of(cmd.getQuantity())),
                new DeliveryTime(cmd.getDeliveryTime()));
    }

    public WeavingOrder composeWeavingOrderFromUpdateCmd(UpdateWeavingOrderCommand cmd,
                                                         WeavingOrder oldOrder,
                                                         ProduceOrder produceOrder,
                                                         SkuCode skuCode) {
        return new WeavingOrder(oldOrder.getId(),
                oldOrder.getFactoryId(),
                oldOrder.getOrderCode(),
                produceOrder.getId(),
                produceOrder.getCode(),
                StyleDemand.of(skuCode, Quantity.of(cmd.getQuantity())),
                oldOrder.getPlannedQuantity(),
                new DeliveryTime(cmd.getDeliveryTime()),
                oldOrder.getStatus());
    }

    public ProduceOrder composeProduceOrderFromCreateCmd(CreateProduceOrderCommand cmd,
                                                         ProduceOrderCode orderCode,
                                                         Map<String, SkuCode> skuCodeMap,
                                                         Customer customer) {
        return ProduceOrder.newOf(
                StringUtils.isNotBlank(cmd.getOutOrderId()) ? new OuterOrderId(cmd.getOutOrderId()) : null,
                orderCode,
                StringUtils.isNotBlank(cmd.getOrgId()) ? new OuterFactory(cmd.getOrgId(), cmd.getOrgName()) : null,
                customer,
                cmd.getStyleDemands().stream().map(this::composeStyleDemand).toList(),
                new DeliveryTime(cmd.getDeliveryTime()),
                StringUtils.isNotBlank(cmd.getManufactureBatch()) ? new ManufactureBatch(cmd.getManufactureBatch()) : null,
                null != cmd.getManufactureDate() ? new ManufactureDate(cmd.getManufactureDate()) : null
        );
    }

    private StyleDemand composeStyleDemand(StyleDemandCommand cmd) {
        Quantity orderQuantity = Quantity.of(cmd.getOrderQuantity());
        return StyleDemand.of(
                new SkuCode(cmd.getSkuCode()),
                (StringUtils.isNotBlank(cmd.getSymbolId()) || StringUtils.isNotBlank(cmd.getSymbol()))
                        ? new Symbol(cmd.getSymbolId(), cmd.getSymbol()) : null,
                StringUtils.isNotBlank(cmd.getStyleCode()) ? new StyleCode(cmd.getStyleCode()) : null,
                (StringUtils.isNotBlank(cmd.getSizeId()) || StringUtils.isNotBlank(cmd.getSize()))
                        ? new Size(cmd.getSizeId(), cmd.getSize()) : null,
                StringUtils.isNotBlank(cmd.getColor()) ? new Color(cmd.getColorId(), cmd.getColor()) : null,
                orderQuantity,
                cmd.getWeaveQuantity() != null ? Quantity.of(cmd.getWeaveQuantity()) : orderQuantity,
                cmd.getSampleQuantity() != null ? Quantity.of(cmd.getSampleQuantity()) : Quantity.zero(),
                null
        );
    }

    public ProduceOrder composeProduceOrderFromUpdateCmd(UpdateProduceOrderCommand cmd,
                                                         ProduceOrder oldOrder,
                                                         Map<String, SkuCode> skuCodeMap,
                                                         Customer customer) {
        return new ProduceOrder(oldOrder.getId(),
                StringUtils.isNotBlank(cmd.getOutOrderId()) ? new OuterOrderId(cmd.getOutOrderId()) : null,
                oldOrder.getCode(),
                StringUtils.isNotBlank(cmd.getOrgId()) ? new OuterFactory(cmd.getOrgId(), cmd.getOrgName()) : null,
                customer,
                cmd.getStyleDemands().stream().map(this::composeStyleDemand).toList(),
                new DeliveryTime(cmd.getDeliveryTime()),
                StringUtils.isNotBlank(cmd.getManufactureBatch()) ? new ManufactureBatch(cmd.getManufactureBatch()) : null,
                null != cmd.getManufactureDate() ? new ManufactureDate(cmd.getManufactureDate()) : null,
                oldOrder.getStatus());
    }

    public SearchWeavingOrder composeSearchWeavingOrder(PageWeavingOrderQuery query) {
        return new SearchWeavingOrder(
                null == query.getFactoryId() ? null : new FactoryId(query.getFactoryId()),
                StringUtils.isNotBlank(query.getCode()) ? new WeavingOrderCode(query.getCode()) : null,
                StringUtils.isNotBlank(query.getStyleCode()) ? new StyleCode(query.getStyleCode()) : null,
                CollectionUtils.isNotEmpty(query.getStatus()) ? query.getStatus().stream().map(WeavingOrderStatus::findByCode).toList() : null,
                query
        );
    }

    public SearchProduceOrder composeSearchProduceOrder(ProduceOrderQuery query) {
        return new SearchProduceOrder(
                null == query.getFactoryId() ? null : new FactoryId(query.getFactoryId()),
                StringUtils.isNotBlank(query.getCode()) ? new ProduceOrderCode(query.getCode()) : null,
                StringUtils.isNotBlank(query.getCustomerCode()) ? new CustomerCode(query.getCustomerCode()) : null,
                CollectionUtils.isNotEmpty(query.getStatus()) ? query.getStatus().stream().map(ProduceOrderStatus::findByCode).toList() : null,
                null == query.getDeliveryTimeStart() ? null : new DeliveryTime(query.getDeliveryTimeStart()),
                null == query.getDeliveryTimeEnd() ? null : new DeliveryTime(query.getDeliveryTimeEnd()),
                StringUtils.isNotBlank(query.getSearch()) ? new Search(query.getSearch()) : null,
                query
        );
    }

    public ProduceOrder composeProduceOrderFromOuterCmd(OperateOuterProduceOrderCommand cmd) {
        return ProduceOrder.newOf(
                StringUtils.isNotBlank(cmd.getOutOrderId()) ? new OuterOrderId(cmd.getOutOrderId()) : null,
                new ProduceOrderCode(cmd.getCode()),
                StringUtils.isNotBlank(cmd.getOrgId()) ? new OuterFactory(cmd.getOrgId(), cmd.getOrgName()) : null,
                null,
                cmd.getStyleDemands().stream().map(this::composeStyleDemand).toList(),
                new DeliveryTime(cmd.getDeliveryTime()),
                StringUtils.isNotBlank(cmd.getManufactureBatch()) ? new ManufactureBatch(cmd.getManufactureBatch()) : null,
                null != cmd.getManufactureDate() ? new ManufactureDate(cmd.getManufactureDate()) : null
        );
    }

    public WeavingPartOrder composeWeavingPartOrderFromOperateCmd(OperateOuterPartOrderCommand cmd,
                                                                  ProduceOrderId produceOrderId,
                                                                  ProduceOrderCode produceOrderCode,
                                                                  DeliveryTime deliveryTime) {
        StylePartDemand demand = new StylePartDemand(
                new SkuCode(cmd.getStyleCode() + cmd.getSize()),
                StringUtils.isNotBlank(cmd.getStyleCode()) ? new StyleCode(cmd.getStyleCode()) : null,
                StringUtils.isNotBlank(cmd.getSymbolId()) && StringUtils.isNotBlank(cmd.getSymbol()) ?
                        new Symbol(cmd.getSymbolId(), cmd.getSymbol()) : null,
                StringUtils.isNotBlank(cmd.getSizeId()) && StringUtils.isNotBlank(cmd.getSize()) ?
                        new Size(cmd.getSizeId(), cmd.getSize()) : null,
                StringUtils.isNotBlank(cmd.getPartId()) && StringUtils.isNotBlank(cmd.getPart()) ?
                        new Part(cmd.getPartId(), cmd.getPart()) : null,
                StringUtils.isNotBlank(cmd.getPartColor()) ?
                        new Color(cmd.getPartColorId(), cmd.getPartColor()) : null,
                Quantity.of(cmd.getOrderQuantity())
        );

        return new WeavingPartOrder(
                null,
                null,
                null,
                produceOrderId,
                produceOrderCode,
                demand,
                Collections.emptyList(),
                Quantity.zero(),
                deliveryTime,
                StringUtils.isNotBlank(cmd.getProgram()) ? new ProgramFile(cmd.getProgram(), null) : null,
                StringUtils.isNotBlank(cmd.getTaskDetailId()) ? new TaskDetailId(cmd.getTaskDetailId()) : null,
                StringUtils.isNotBlank(cmd.getFigure()) ? new Figure(cmd.getFigure()) : null,
                StringUtils.isNotBlank(cmd.getUnit()) ? new Unit(cmd.getUnit()) : null,
                StringUtils.isNotBlank(cmd.getComment()) ? new OrderComment(cmd.getComment()) : null,
                WeavingOrderStatus.CREATED
        );
    }
}
