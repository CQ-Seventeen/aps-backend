package com.santoni.iot.aps.adapter.plan.controller;

import com.santoni.iot.aps.adapter.plan.request.factory.AssignOrderToFactoryRequest;
import com.santoni.iot.aps.adapter.plan.request.factory.FactoryPlanByOrderRequest;
import com.santoni.iot.aps.adapter.plan.request.factory.FactoryProducingOrderRequest;
import com.santoni.iot.aps.application.order.dto.ProduceOrderDTO;
import com.santoni.iot.aps.application.plan.FactoryPlanOperateApplication;
import com.santoni.iot.aps.application.plan.FactoryPlanQueryApplication;
import com.santoni.iot.aps.application.plan.command.AssignOrderToFactoryCommand;
import com.santoni.iot.aps.application.plan.dto.factory.FactoryOrderDimPlanDTO;
import com.santoni.iot.aps.application.plan.query.FactoryOrderQuery;
import com.santoni.iot.aps.application.plan.query.FactoryPlanQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/plan/factory")
@RestController
public class FactoryPlanController {

    @Autowired
    private FactoryPlanQueryApplication factoryPlanQueryApplication;

    @Autowired
    private FactoryPlanOperateApplication factoryPlanOperateApplication;

    @PostMapping("/produce_order_dim")
    @ResponseBody
    @SantoniHeader
    public ReturnData<FactoryOrderDimPlanDTO> queryPlanByOrderDim(@RequestBody FactoryPlanByOrderRequest request) {
        var query = new FactoryPlanQuery();
        query.setFactoryId(request.getFactoryId());
        query.setProduceOrderId(request.getProduceOrderId());
        query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? null : TimeUtil.fromGeneralString(request.getStartTime()));
        query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? null : TimeUtil.fromGeneralString(request.getEndTime()));
        try {
            return new ReturnData<>(factoryPlanQueryApplication.queryFactoryPlanOrderDim(query));
        } catch (Exception e) {
            log.error("FactoryPlan ProduceOrderDim query error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/assign_order")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> assignOrderToFactory(@RequestBody AssignOrderToFactoryRequest request) {
        var cmd = new AssignOrderToFactoryCommand();
        cmd.setProduceOrderId(request.getProduceOrderId());
        cmd.setFactoryId(request.getFactoryId());
        try {
            factoryPlanOperateApplication.assignOrderToFactory(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Assign ProduceOrderToFactory error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Assign ProduceOrderToFactory error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/order_producing")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProduceOrderDTO>> queryProducingOrder(@RequestHeader("factoryId") Long factoryId,
                                                                 @RequestBody FactoryProducingOrderRequest request) {
        if (null == factoryId || factoryId <= 0) {
            return new ReturnData<>(400, "无权查看");
        }
        var query = new FactoryOrderQuery();
        query.setFactoryId(factoryId);
        query.setDate(request.getDate());
        try {
            return new ReturnData<>(factoryPlanQueryApplication.queryOrderInFactory(query));
        } catch (IllegalArgumentException iae) {
            log.error("FactoryPlan Order In Producing query error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("FactoryPlan Order In Producing query error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

}
