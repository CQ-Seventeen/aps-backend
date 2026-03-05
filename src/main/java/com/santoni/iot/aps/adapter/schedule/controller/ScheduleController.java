package com.santoni.iot.aps.adapter.schedule.controller;

import com.santoni.iot.aps.adapter.schedule.request.*;
import com.santoni.iot.aps.application.schedule.ScheduleQueryApplication;
import com.santoni.iot.aps.application.schedule.ScheduleTaskApplication;
import com.santoni.iot.aps.application.schedule.command.DoScheduleCommand;
import com.santoni.iot.aps.application.schedule.command.StyleWeaveDemandCommand;
import com.santoni.iot.aps.application.schedule.command.SubmitScheduleCommand;
import com.santoni.iot.aps.application.schedule.dto.ScheduleLogDTO;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskDTO;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskResultDTO;
import com.santoni.iot.aps.application.schedule.query.PageScheduleLogQuery;
import com.santoni.iot.aps.application.schedule.query.PageScheduleTaskQuery;
import com.santoni.iot.aps.application.schedule.query.ScheduleTaskResultQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.infrastructure.threadpool.ScheduleSolveThreadPool;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleTaskApplication scheduleTaskApplication;

    @Autowired
    private ScheduleQueryApplication scheduleQueryApplication;

    @PostMapping("/submit_task")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> submitScheduleTask(@RequestBody SubmitScheduleTaskRequest request) {
        var command = new SubmitScheduleCommand();
        command.setMachineIds(request.getMachineIds());
        command.setStartTime(TimeUtil.fromGeneralString(request.getStartTime()));
        command.setEndTime(TimeUtil.fromGeneralString(request.getEndTime()));
        command.setStyleDemandList(request.getStyleDemandList().stream().map(this::convertToStyleWeaveDemandCommand).toList());
        try {
            scheduleTaskApplication.submitScheduleTask(command);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Submit ScheduleTask error, bad request:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Submit ScheduleTask error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<ScheduleTaskDTO>> pageQueryScheduleTask(@RequestHeader("instituteId") long instituteId,
                                                                         @RequestBody PageQueryScheduleTaskRequest request) {
        try {
            var query = new PageScheduleTaskQuery();
            query.copyPageParamFromPageRequest(request);
            query.setStatus(request.getStatusList());
            return new ReturnData<>(scheduleTaskApplication.pageQueryScheduleTask(query));
        } catch (IllegalArgumentException iae) {
            log.error("PageQuery ScheduleTask error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("PageQuery ScheduleTask error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/get_result")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ScheduleTaskResultDTO> getScheduleTaskResult(@RequestBody QuerySolveResultRequest request) {
        try {
            var query = new ScheduleTaskResultQuery();
            query.setTaskId(request.getTaskId());
            return new ReturnData<>(scheduleTaskApplication.getScheduleTaskResult(query));
        } catch (IllegalArgumentException iae) {
            log.error("Get ScheduleTaskResult error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Get ScheduleTaskResult error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/manually_solve")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> manuallySolveScheduleTask(@RequestBody ManuallySolveScheduleRequest request) {
        try {
            var command = new DoScheduleCommand();
            command.setTaskId(request.getTaskId());
            ScheduleSolveThreadPool.submitSolveTask(() -> scheduleTaskApplication.doScheduleSolve(command));
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("ManuallySolveScheduleTask error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/log/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<ScheduleLogDTO>> pageQueryScheduleLog(@RequestBody PageQueryScheduleLogRequest request) {
        try {
            var query = new PageScheduleLogQuery();
            query.copyPageParamFromPageRequest(request);
            query.setType(request.getType());
            return new ReturnData<>(scheduleQueryApplication.pageQueryScheduleLog(query));
        } catch (Exception e) {
            log.error("PageQuery ScheduleLog error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/direct_recommend")
    @ResponseBody
    @SantoniHeader
    public ReturnData<ScheduleTaskResultDTO> directRecommend(@RequestBody DirectRecommendRequest request) {
        try {
            var command = new SubmitScheduleCommand();
            command.setMachineIds(request.getMachineIds());
            command.setStartTime(TimeUtil.fromGeneralString(request.getStartTime()));
            command.setEndTime(TimeUtil.fromGeneralString(request.getEndTime()));
            command.setStyleDemandList(request.getStyleDemandList().stream().map(this::convertToStyleWeaveDemandCommand).toList());
            return new ReturnData<>(scheduleTaskApplication.directRecommend(command));
        } catch (Exception e) {
            log.error("PageQuery ScheduleLog error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    private StyleWeaveDemandCommand convertToStyleWeaveDemandCommand(StyleWeaveDemandRequest request) {
        var command = new StyleWeaveDemandCommand();
        command.setWeavingPartOrderId(request.getWeavingPartOrderId());
        command.setQuantity(request.getQuantity());
        return command;
    }
}
