package com.santoni.iot.aps.adapter.plan.controller;

import com.santoni.iot.aps.adapter.plan.request.order.OrderPlanTrackRequest;
import com.santoni.iot.aps.adapter.plan.request.order.ProducePredictRequest;
import com.santoni.iot.aps.application.plan.PlanTrackApplication;
import com.santoni.iot.aps.application.plan.dto.track.ProducePlanTrackDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePredictDTO;
import com.santoni.iot.aps.application.plan.query.DailyYarnPredictQuery;
import com.santoni.iot.aps.application.plan.query.ProducePlanTrackQuery;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/plan")
@RestController
public class PlanTrackController {

    @Autowired
    private PlanTrackApplication planTrackApplication;

    @PostMapping("/producing/plan_track")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProducePlanTrackDTO>> dailyPlanTrack(@RequestHeader("instituteId") long instituteId,
                                                                @RequestBody OrderPlanTrackRequest request) {
        var query = new ProducePlanTrackQuery();
        query.setDate(request.getDate());
        try {
            return new ReturnData<>(planTrackApplication.queryProducePlanTrack(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("ProducePlan WeavingView error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/producing/predict")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ProducePredictDTO> dailyProducePredict(@RequestHeader("instituteId") long instituteId,
                                                             @RequestBody ProducePredictRequest request) {
        var query = new DailyYarnPredictQuery();
        query.setDate(request.getDate());
        try {
            return new ReturnData<>(planTrackApplication.dailyYarnPredict(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("ProducePlan WeavingView error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/producing/aggregate_by_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ProducePredictDTO> produceTrackAggregate(@RequestHeader("instituteId") long instituteId,
                                                             @RequestBody ProducePredictRequest request) {
        var query = new DailyYarnPredictQuery();
        query.setDate(request.getDate());
        try {
            return new ReturnData<>(planTrackApplication.dailyYarnPredict(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("ProducePlan WeavingView error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }
}