package com.santoni.iot.aps.adapter.plan.controller;

import com.santoni.iot.aps.adapter.plan.request.machine.*;
import com.santoni.iot.aps.adapter.resource.request.MachineCapacityRequest;
import com.santoni.iot.aps.application.plan.FactoryPlanQueryApplication;
import com.santoni.iot.aps.application.plan.PlanOperateApplication;
import com.santoni.iot.aps.application.plan.command.BatchAssignTaskCommand;
import com.santoni.iot.aps.application.plan.command.FreeUpTimeCommand;
import com.santoni.iot.aps.application.plan.command.ManuallyAssignTaskCommand;
import com.santoni.iot.aps.application.plan.command.MachineAssignTaskCommand;
import com.santoni.iot.aps.application.plan.command.TaskAssignDetail;
import com.santoni.iot.aps.application.plan.dto.factory.MachineCapacityDTO;
import com.santoni.iot.aps.application.plan.query.ComplementAdviceQuery;
import com.santoni.iot.aps.application.plan.query.MachineCapacityQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/plan")
@RestController
public class PlanController {

    @Autowired
    private PlanOperateApplication planOperateApplication;

    @Autowired
    private FactoryPlanQueryApplication factoryPlanQueryApplication;

    @PostMapping("/assign_single_task")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> manuallyAssignTask(@RequestBody ManuallyAssignTaskRequest request) {
        var cmd = new ManuallyAssignTaskCommand();
        cmd.setWeavingPartOrderId(request.getWeavingPartOrderId());
        cmd.setMachineId(request.getMachineId());
        cmd.setPlannedQuantity(request.getQuantity());
        cmd.setPlanStartTime(TimeUtil.fromGeneralString(request.getPlanStartTime()));
        cmd.setPlanEndTime(TimeUtil.fromGeneralString(request.getPlanEndTime()));
        try {
            planOperateApplication.manuallyAssignTask(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Manually Assign Task error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Manually Assign Task error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/batch_assign_task")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> batchAssignTask(@RequestBody BatchAssignTaskRequest request) {
        var cmd = convertBatchAssignTaskRequestToCommand(request);

        try {
            planOperateApplication.batchAssignTask(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Batch Assign Task error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Batch Assign Task error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/free_up_time")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> freeUpMachineTime(@RequestBody FreeMachineTimeRequest request) {
        var cmd = new FreeUpTimeCommand();
        cmd.setMachineIds(request.getMachineIds());
        cmd.setStartTime(TimeUtil.fromGeneralString(request.getStartTime()));
        cmd.setEndTime(TimeUtil.fromGeneralString(request.getEndTime()));
        try {
            planOperateApplication.freeUpTime(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("FreeUp Machine Time error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("FreeUp Machine Time error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/machine_capacity")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<MachineCapacityDTO>> queryMachineCapacity(@RequestHeader("instituteId") long instituteId,
                                                                     @RequestBody MachineCapacityRequest request) {
        try {
            var query = new MachineCapacityQuery();
            query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? TimeUtil.getStartOfToday() : TimeUtil.fromGeneralString(request.getStartTime()));
            query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? TimeUtil.getStartOfDaysAfter(10) : TimeUtil.fromGeneralString(request.getEndTime()));
            return new ReturnData<>(factoryPlanQueryApplication.queryFactoryMachineCapacity(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query MachineCapacity error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query MachineCapacity error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/complement")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> complement(@RequestHeader("instituteId") long instituteId,
                                       @RequestBody MachineCapacityRequest request) {
        try {
            var query = new MachineCapacityQuery();
            query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? TimeUtil.getStartOfToday() : TimeUtil.fromGeneralString(request.getStartTime()));
            query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? TimeUtil.getStartOfDaysAfter(10) : TimeUtil.fromGeneralString(request.getEndTime()));
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Complement error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query MachineCapacity error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/advice_for_complement")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> complementAdvice(@RequestHeader("instituteId") long instituteId,
                                             @RequestBody AdviceComplementRequest request) {
        try {
            var query = new ComplementAdviceQuery();

            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Advice for complement error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query MachineCapacity error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/manual_sync_task")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> manualSyncTask(@RequestHeader("instituteId") long instituteId,
                                           @RequestBody SyncTaskRequest request) {
        try {
            planOperateApplication.manualSyncTask(request.getTaskId());
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Manual Sync Task error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Manual Sync Task error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    private BatchAssignTaskCommand convertBatchAssignTaskRequestToCommand(BatchAssignTaskRequest request) {
        var cmd = new BatchAssignTaskCommand();
        cmd.setStartTime(TimeUtil.fromGeneralString(request.getStartTime()));
        cmd.setEndTime(TimeUtil.fromGeneralString(request.getEndTime()));
        if (CollectionUtils.isNotEmpty(request.getMachineTaskList())) {
            cmd.setMachineAssignList(request.getMachineTaskList().stream().map(this::convertMachineAssignTaskReqToCmd).toList());
        }

        return cmd;
    }

    private MachineAssignTaskCommand convertMachineAssignTaskReqToCmd(MachineAssignTaskReq req) {
        var cmd = new MachineAssignTaskCommand();
        cmd.setMachineId(req.getMachineId());
        if (CollectionUtils.isNotEmpty(req.getTaskList())) {
            cmd.setAssignList(req.getTaskList().stream().map(taskReq -> {
                var taskDetail = new TaskAssignDetail();
                taskDetail.setWeavingPartOrderId(taskReq.getWeavingPartOrderId());
                taskDetail.setQuantity(taskReq.getQuantity());
                taskDetail.setStartTime(TimeUtil.fromGeneralString(taskReq.getPlanStartTime()));
                taskDetail.setEndTime(TimeUtil.fromGeneralString(taskReq.getPlanEndTime()));
                return taskDetail;
            }).toList());
        }
        return cmd;
    }
}
