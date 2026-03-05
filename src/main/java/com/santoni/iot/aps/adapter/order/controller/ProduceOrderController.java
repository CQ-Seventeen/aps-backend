package com.santoni.iot.aps.adapter.order.controller;

import com.santoni.iot.aps.application.order.OrderOperateApplication;
import com.santoni.iot.aps.application.order.OrderQueryApplication;
import com.santoni.iot.aps.application.order.command.CreateProduceOrderCommand;
import com.santoni.iot.aps.application.order.command.StyleDemandCommand;
import com.santoni.iot.aps.application.order.command.UpdateProduceOrderCommand;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.order.query.ProduceOrderDetailByCodeQuery;
import com.santoni.iot.aps.application.order.query.ProduceOrderQuery;
import com.santoni.iot.aps.application.order.query.ProduceOrderDetailQuery;
import com.santoni.iot.aps.application.plan.dto.factory.ProduceOrderDemandDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.adapter.order.request.CreateProduceOrderRequest;
import com.santoni.iot.aps.adapter.order.request.PageQueryProduceOrderRequest;
import com.santoni.iot.aps.adapter.order.request.UpdateProduceOrderRequest;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/order/produce")
@RestController
@Slf4j
public class ProduceOrderController {

    @Autowired
    private OrderQueryApplication orderQueryApplication;

    @Autowired
    private OrderOperateApplication orderOperateApplication;

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<ProduceOrderDTO>> pageQueryProduceOrder(@RequestBody PageQueryProduceOrderRequest request) {
        return queryProduceOrder(request, null);
    }

    @PostMapping("/factory/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<ProduceOrderDTO>> pageQueryProduceOrderByFactory(@RequestHeader("factoryId") Long factoryId,
                                                                                  @RequestBody PageQueryProduceOrderRequest request) {
        if (null == factoryId || factoryId <= 0) {
            return new ReturnData<>(400, "无权查看");
        }
        return queryProduceOrder(request, factoryId);
    }

    private ReturnData<PageResult<ProduceOrderDTO>> queryProduceOrder(PageQueryProduceOrderRequest request, Long factoryId) {
        var query = new ProduceOrderQuery();
        query.copyPageParamFromPageRequest(request);
        query.setCode(request.getOrderCode());
        query.setCustomerCode(request.getCustomerCode());
        query.setStatus(request.getStatus());
        if (null != factoryId) {
            query.setFactoryId(factoryId);
        }
        try {
            return new ReturnData<>(orderQueryApplication.pageQueryProduceOrder(query));
        } catch (Exception e) {
            log.error("Page query ProduceOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createProduceOrder(@RequestHeader("instituteId") long instituteId, @RequestBody CreateProduceOrderRequest request) {
        var cmd = new CreateProduceOrderCommand();
        fillOperateProduceOrderCmdByRequest(cmd, request);
        try {
            orderOperateApplication.createProduceOrder(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Create ProduceOrder error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Create ProduceOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateProduceOrder(@RequestHeader("instituteId") long instituteId, @RequestBody UpdateProduceOrderRequest request) {
        var cmd = new UpdateProduceOrderCommand();
        fillOperateProduceOrderCmdByRequest(cmd, request);
        cmd.setOrderId(request.getOrderId());
        try {
            orderOperateApplication.updateProduceOrder(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Update ProduceOrder error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Update ProduceOrder error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @GetMapping("/detail_by_code")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ProduceOrderDTO> queryProduceOrderByCode(@RequestParam("orderCode") String orderCode) {
        var query = new ProduceOrderDetailByCodeQuery(orderCode);
        try {
            return new ReturnData<>(orderQueryApplication.queryProduceOrderByCode(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query ProduceOrder ByCode error, bad req:{}", JacksonUtil.toJson(query));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query ProduceOrder ByCode error, req:{}", JacksonUtil.toJson(query), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @GetMapping("/demand")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ProduceOrderDemandDTO> queryProduceDemandByCode(@RequestParam("orderId") long orderId) {
        var query = new ProduceOrderDetailQuery(orderId);
        try {
            return new ReturnData<>(orderQueryApplication.queryProduceOrderDemand(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query ProduceOrder Demand error, bad req:{}", JacksonUtil.toJson(query));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query ProduceOrder Demand error, req:{}", JacksonUtil.toJson(query), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    private void fillOperateProduceOrderCmdByRequest(CreateProduceOrderCommand cmd, CreateProduceOrderRequest request) {
        cmd.setCode(request.getCode());
        cmd.setCustomerCode(request.getCustomerCode());
        cmd.setStyleDemands(request.getStyleDemands().stream().map(it -> {
            var styleCmd = new StyleDemandCommand();
            styleCmd.setSkuCode(it.getSkuCode());
            styleCmd.setColor(it.getColor());
            styleCmd.setOrderQuantity(it.getOrderQuantity());
            styleCmd.setWeaveQuantity(it.getWeaveQuantity());
            styleCmd.setSampleQuantity(it.getSampleQuantity());
            return styleCmd;
        }).toList());
        cmd.setDeliveryTime(TimeUtil.fromGeneralString(request.getDeliveryTime()));
    }

}
