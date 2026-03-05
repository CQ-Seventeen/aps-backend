package com.santoni.iot.aps.adapter.execute.controller;

import com.santoni.iot.aps.adapter.execute.request.EstimateMaxQuantityRequest;
import com.santoni.iot.aps.adapter.execute.request.EstimateWeaveTimeRequest;
import com.santoni.iot.aps.application.execute.ExecuteQueryApplication;
import com.santoni.iot.aps.application.execute.query.EstimateMaxWaveQuantityQuery;
import com.santoni.iot.aps.application.resource.dto.EstimateTimeDTO;
import com.santoni.iot.aps.application.execute.query.EstimateMachineWeaveTimeQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/execute")
@RestController
@Slf4j
public class ExecuteController {

    @Autowired
    private ExecuteQueryApplication executeQueryApplication;

    @PostMapping("/estimate_weave_time")
    @ResponseBody
    @SantoniHeader
    public ReturnData<EstimateTimeDTO> estimateWeaveTime(@RequestHeader("instituteId") long instituteId,
                                                         @RequestBody EstimateWeaveTimeRequest request) {
        if (request.getQuantity() <= 0) {
            return new ReturnData<>(400, "计划数量必须大于0");
        }
        if (StringUtils.isBlank(request.getCanStartTime()) || StringUtils.isBlank(request.getCanEndTime())) {
            return new ReturnData<>(400, "必须指定计划时间段");
        }
        try {
            var query = new EstimateMachineWeaveTimeQuery();
            query.setMachineId(request.getMachineId());
            query.setWeavingPartOrderId(request.getWeavingPartOrderId());
            query.setQuantity(request.getQuantity());
            query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? null : TimeUtil.fromGeneralString(request.getStartTime()));
            query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? null : TimeUtil.fromGeneralString(request.getEndTime()));
            query.setCanStartTime(TimeUtil.fromGeneralString(request.getCanStartTime()));
            query.setCanEndTime(TimeUtil.fromGeneralString(request.getCanEndTime()));
            return new ReturnData<>(executeQueryApplication.estimateWeaveTime(query));
        } catch (IllegalArgumentException iae) {
            log.error("Estimate Machine Weave Time error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Estimate Machine Weave Time error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/estimate_max_quantity")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Integer> estimateMaxQuantity(@RequestHeader("instituteId") long instituteId,
                                                         @RequestBody EstimateMaxQuantityRequest request) {
        if (request.getNeedQuantity() <= 0) {
            return new ReturnData<>(400, "计划数量必须大于0");
        }
        if (StringUtils.isBlank(request.getStartTime()) || StringUtils.isBlank(request.getEndTime())) {
            return new ReturnData<>(400, "必须指定计划时间段");
        }
        try {
            var query = new EstimateMaxWaveQuantityQuery();
            query.setMachineId(request.getMachineId());
            query.setWeavingPartOrderId(request.getWeavingPartOrderId());
            query.setNeedQuantity(request.getNeedQuantity());
            query.setStartTime(TimeUtil.fromGeneralString(request.getStartTime()));
            query.setEndTime(TimeUtil.fromGeneralString(request.getEndTime()));
            return new ReturnData<>(executeQueryApplication.estimateMaxWeaveQuantity(query));
        } catch (IllegalArgumentException iae) {
            log.error("Estimate Machine MaxWeaveQuantity error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Estimate Machine MaxWeaveQuantity error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }
}
