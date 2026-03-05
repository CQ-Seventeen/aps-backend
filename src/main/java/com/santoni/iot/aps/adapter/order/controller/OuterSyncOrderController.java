package com.santoni.iot.aps.adapter.order.controller;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.adapter.order.request.SyncProduceOrderReq;
import com.santoni.iot.aps.adapter.order.request.SyncProduceStyleReq;
import com.santoni.iot.aps.adapter.order.request.SyncProducePartReq;
import com.santoni.iot.aps.application.order.OrderOperateApplication;
import com.santoni.iot.aps.application.order.command.StyleDemandCommand;
import com.santoni.iot.aps.application.order.command.OperateOuterProduceOrderCommand;
import com.santoni.iot.aps.application.order.command.OperateOuterPartOrderCommand;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import com.santoni.iot.utils.record.constant.Header;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/sync")
@RestController
@Slf4j
public class OuterSyncOrderController {

    @Autowired
    private OrderOperateApplication orderOperateApplication;

    @PostMapping("/manufacture_order")
    @ResponseBody
    @SantoniHeader(Header.NONE)
    public ReturnData<Void> syncManufactureOrder(@RequestBody SyncProduceOrderReq req) {
        log.info("Receive SyncProduceOrderReq: {}", req);
        try {
            PlanContext.setInstituteId(1L);
            OperateOuterProduceOrderCommand cmd = convertToCommand(req);
            orderOperateApplication.importOuterProduceOrder(cmd);
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("SyncProduceOrderReq error", e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    private OperateOuterProduceOrderCommand convertToCommand(SyncProduceOrderReq req) {
        OperateOuterProduceOrderCommand cmd = new OperateOuterProduceOrderCommand();
        cmd.setOutOrderId(req.getManufactureOrderId());
        cmd.setCode(req.getManufactureOrder());
        cmd.setOrgId(req.getOrganizationId());
        cmd.setOrgName(req.getOrganizationName());
        cmd.setCustomerCode(req.getOrganizationId()); // 使用 organizationId 作为 customerCode
        cmd.setDeliveryTime(TimeUtil.fromYYYYMMDD(req.getDeliveryDate()));
        cmd.setManufactureBatch(req.getManufactureBatch());
        cmd.setManufactureDate(TimeUtil.fromYYYYMMDD(req.getManufactureDate()));

        // 第一步：构建以 partReq 为基准的冗余结构
        List<PartOrderWithStyleInfo> partOrdersWithStyle = new ArrayList<>();
        for (SyncProduceStyleReq styleReq : req.getStyleList()) {
            for (SyncProducePartReq partReq : styleReq.getPartList()) {
                partOrdersWithStyle.add(new PartOrderWithStyleInfo(styleReq, partReq));
            }
        }

        Map<String, StyleDemandCommand> styleDemandMap = getStringStyleDemandCommandMap(partOrdersWithStyle);
        cmd.setStyleDemands(new ArrayList<>(styleDemandMap.values()));

        List<OperateOuterPartOrderCommand> partOrders = partOrdersWithStyle.stream()
                .map(item -> {
                    OperateOuterPartOrderCommand partOrder = new OperateOuterPartOrderCommand();
                    partOrder.setStyleId(item.styleReq.getStyleId());
                    partOrder.setStyleName(item.styleReq.getStyleName());
                    partOrder.setStyleCode(item.styleReq.getStyleCode());
                    partOrder.setSymbolId(item.styleReq.getSymbolId());
                    partOrder.setSymbol(item.styleReq.getSymbol());
                    partOrder.setPart(item.partReq.getPart());
                    partOrder.setPartId(item.partReq.getPartId());
                    partOrder.setProgram(item.partReq.getProgram());
                    partOrder.setPartColorId(item.partReq.getPartColorId());
                    partOrder.setPartColor(item.partReq.getPartColor());
                    partOrder.setSizeId(item.partReq.getSizeId());
                    partOrder.setSize(item.partReq.getSize());
                    partOrder.setFigure(item.partReq.getFigure());
                    partOrder.setOrderQuantity(item.partReq.getOrderQuantity());
                    partOrder.setUnit(item.partReq.getUnit());
                    partOrder.setComment(item.partReq.getComment());
                    partOrder.setTaskDetailId(item.partReq.getTaskDtlId());
                    return partOrder;
                })
                .collect(Collectors.toList());
        cmd.setPartOrders(partOrders);

        return cmd;
    }

    @NotNull
    private static Map<String, StyleDemandCommand> getStringStyleDemandCommandMap(List<PartOrderWithStyleInfo> partOrdersWithStyle) {
        Map<String, StyleDemandCommand> styleDemandMap = Maps.newHashMap();
        for (PartOrderWithStyleInfo item : partOrdersWithStyle) {
            String sizeKey = item.partReq.getSize();
            styleDemandMap.computeIfAbsent(sizeKey, k -> {
                var demand = new StyleDemandCommand();
                demand.setSkuCode(item.styleReq.getStyleCode() + item.partReq.getSize());
                demand.setStyleCode(item.styleReq.getStyleCode());
                demand.setSizeId(item.partReq.getSizeId());
                demand.setSize(item.partReq.getSize());
                demand.setSymbolId(item.styleReq.getSymbolId());
                demand.setSymbol(item.styleReq.getSymbol());
                demand.setColorId(item.partReq.getPartColorId());
                demand.setColor(item.partReq.getPartColor());
                demand.setOrderQuantity(item.partReq.getOrderQuantity());
                demand.setWeaveQuantity(item.partReq.getOrderQuantity());
                demand.setSampleQuantity(0);
                return demand;
            });
        }
        return styleDemandMap;
    }

    private static class PartOrderWithStyleInfo {
        SyncProduceStyleReq styleReq;
        SyncProducePartReq partReq;

        PartOrderWithStyleInfo(SyncProduceStyleReq styleReq, SyncProducePartReq partReq) {
            this.styleReq = styleReq;
            this.partReq = partReq;
        }
    }
}
