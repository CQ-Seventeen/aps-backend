package com.santoni.iot.aps.adapter.order.controller;

import com.santoni.iot.aps.adapter.order.request.ListWeavingOrderByIdRequest;
import com.santoni.iot.aps.application.order.OrderOperateApplication;
import com.santoni.iot.aps.application.order.OrderQueryApplication;
import com.santoni.iot.aps.application.order.command.CreateWeavingOrderCommand;
import com.santoni.iot.aps.application.order.command.UpdateWeavingOrderCommand;
import com.santoni.iot.aps.application.order.dto.WeavingOrderDTO;
import com.santoni.iot.aps.application.order.query.CanWeaveMachineQuery;
import com.santoni.iot.aps.application.order.query.ListWeavingOrderByIdQuery;
import com.santoni.iot.aps.application.order.query.PageWeavingOrderQuery;
import com.santoni.iot.aps.application.order.query.WeavingOrderDetailQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.adapter.order.request.CreateWeavingOrderRequest;
import com.santoni.iot.aps.adapter.order.request.PageQueryWeavingOrderRequest;
import com.santoni.iot.aps.adapter.order.request.UpdateWeavingOrderRequest;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order/weaving")
@RestController
@Slf4j
public class WeavingOrderController {

    @Autowired
    private OrderQueryApplication orderQueryApplication;

    @Autowired
    private OrderOperateApplication orderOperateApplication;

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<WeavingOrderDTO>> pageQueryWeavingOrder(@RequestHeader("factoryId") Long factoryId,
                                                                         @RequestBody PageQueryWeavingOrderRequest request) {
        var query = new PageWeavingOrderQuery();
        query.copyPageParamFromPageRequest(request);
        if (null != factoryId && factoryId != 0) {
            query.setFactoryId(factoryId);
        }
        query.setCode(request.getOrderCode());
        query.setStyleCode(request.getStyleCode());
        query.setStatus(request.getStatus());
        try {
            return new ReturnData<>(orderQueryApplication.pageQueryWeavingOrder(query));
        } catch (Exception e) {
            log.error("Page query WeavingOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createWeavingOrder(@RequestBody CreateWeavingOrderRequest request) {
        var cmd = new CreateWeavingOrderCommand();
        fillOperateWeavingOrderCmdByRequest(cmd, request);
        try {
            orderOperateApplication.createWeavingOrder(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Create WeavingOrder error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Create WeavingOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateWeavingOrder(@RequestBody UpdateWeavingOrderRequest request) {
        var cmd = new UpdateWeavingOrderCommand();
        fillOperateWeavingOrderCmdByRequest(cmd, request);
        cmd.setOrderId(request.getOrderId());
        try {
            orderOperateApplication.updateWeavingOrder(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Update WeavingOrder error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Update WeavingOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @GetMapping("/can_weave_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<Long>> canWeaveMachine(@RequestParam("orderId") long orderId) {
        var query = new CanWeaveMachineQuery(orderId);
        try {
            return new ReturnData<>(orderQueryApplication.canWeaveMachine(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("WeavingOrder Detail error, req:{}", orderId, e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/list_by_id")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<WeavingOrderDTO>> listWeavingOrderByIds(@RequestBody ListWeavingOrderByIdRequest request) {
        try {
            var query = new ListWeavingOrderByIdQuery(request.getWeavingOrderIds());
            return new ReturnData<>(orderQueryApplication.listWeavingOrderById(query));
        } catch (Exception e) {
            log.error("ListWeavingOrderById error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    private void fillOperateWeavingOrderCmdByRequest(CreateWeavingOrderCommand cmd, CreateWeavingOrderRequest request) {
        cmd.setCode(request.getCode());
        cmd.setProduceOrderId(request.getProduceOrderId());
        cmd.setSkuCode(request.getSkuCode());
        cmd.setQuantity(request.getQuantity());
        cmd.setDeliveryTime(TimeUtil.fromGeneralString(request.getDeliveryTime()));
    }
}
