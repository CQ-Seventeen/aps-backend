package com.santoni.iot.aps.adapter.plan.controller;

import com.santoni.iot.aps.adapter.plan.convertor.PlanConvertor;
import com.santoni.iot.aps.adapter.plan.request.machine.CanScheduleMachinePlanRequest;
import com.santoni.iot.aps.adapter.plan.request.machine.CanWeaveMachinePlanRequest;
import com.santoni.iot.aps.adapter.plan.request.order.OrderPlanFilterByMachineRequest;
import com.santoni.iot.aps.adapter.plan.request.order.ProduceOrderPlanRequest;
import com.santoni.iot.aps.application.plan.MachinePlanQueryApplication;
import com.santoni.iot.aps.application.plan.PlanTrackApplication;
import com.santoni.iot.aps.application.plan.dto.machine.MachineLevelDetailPlanDTO;
import com.santoni.iot.aps.application.plan.dto.order.ProduceLevelPlanDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePlanTrackDTO;
import com.santoni.iot.aps.application.plan.query.CanScheduleMachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.OrderPlanFilterByMachineQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/plan")
@RestController
public class OrderPlanController {

    @Autowired
    private MachinePlanQueryApplication machinePlanQueryApplication;

    @Autowired
    private PlanTrackApplication planTrackApplication;

    @GetMapping("/produce_order/unFinished")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProduceLevelPlanDTO>> queryUnPlannedWeavingOrder() {
        try {
            var res = planTrackApplication.queryUnFinishedOrderPlan();
            return new ReturnData<>(res);
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("query unFinishedOrderPlan error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/produce_order/weaving")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProducePlanTrackDTO>> queryProduceOrderPlan(@RequestHeader("instituteId") long instituteId,
                                                                       @RequestHeader("factoryId") Long factoryId,
                                                                       @RequestBody ProduceOrderPlanRequest request) {
        if (null == factoryId || factoryId <= 0) {
            return new ReturnData<>(400, "无权查看");
        }
        try {
            var query = PlanConvertor.convertToProduceOrderQuery(request);
            query.setFactoryId(factoryId);
            var res = planTrackApplication.queryProduceOrderPlan(query);
            return new ReturnData<>(res);
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("ProducePlan WeavingView error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/order/can_weave_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<MachineLevelDetailPlanDTO> canWeaveMachinePlan(@RequestHeader("factoryId") Long factoryId,
                                                                     @RequestBody CanWeaveMachinePlanRequest request) {
        if (null == factoryId || factoryId <= 0) {
            return new ReturnData<>(400, "无权查看");
        }
        try {
            var query = PlanConvertor.convertCanWeaveMachineRequestToQuery(request);
            return new ReturnData<>(machinePlanQueryApplication.canWeaveMachinePlan(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query MachinePlanDetail error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/order/can_schedule_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<MachineLevelDetailPlanDTO> canScheduleMachinePlan(@RequestHeader("factoryId") Long factoryId,
                                                                        @RequestBody CanScheduleMachinePlanRequest request) {
        try {
            var query = new CanScheduleMachinePlanQuery();
            query.setWeavingPartOrderIds(request.getWeavingPartOrderIds());
            return new ReturnData<>(machinePlanQueryApplication.canScheduleMachinePlan(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("CanScheduleMachinePlan error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/weaving/filter_by_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProducePlanTrackDTO>> query(@RequestHeader("instituteId") long instituteId,
                                                       @RequestHeader("factoryId") Long factoryId,
                                                       @RequestBody OrderPlanFilterByMachineRequest request) {
        var query = new OrderPlanFilterByMachineQuery();
        query.setFactoryId(factoryId);
        query.setCylinderDiameterList(request.getCylinderDiameterList());
        query.setNeedleSpacingList(request.getNeedleSpacingList());
        query.setProduceOrderCode(request.getProduceOrderCode());
        try {
            return new ReturnData<>(planTrackApplication.queryOrderPlanFilterByMachine(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("ProducePlan WeavingView error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

}
