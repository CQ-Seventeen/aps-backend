package com.santoni.iot.aps.adapter.plan.controller;

import com.santoni.iot.aps.adapter.plan.convertor.PlanConvertor;
import com.santoni.iot.aps.adapter.plan.request.machine.*;
import com.santoni.iot.aps.application.plan.MachinePlanQueryApplication;
import com.santoni.iot.aps.application.plan.PlanOperateApplication;
import com.santoni.iot.aps.application.plan.command.CancelTaskCommand;
import com.santoni.iot.aps.application.plan.command.UpdateTaskCommand;
import com.santoni.iot.aps.application.plan.dto.machine.*;
import com.santoni.iot.aps.application.plan.query.FreeSoonMachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.MachineTaskDetailQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
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
@RequestMapping("/plan/machine")
@RestController
public class MachinePlanController {

    @Autowired
    private MachinePlanQueryApplication machinePlanQueryApplication;

    @Autowired
    private PlanOperateApplication planOperateApplication;

    @PostMapping("/free_soon_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<AggregateMachinePlanDTO>> queryMachineFreeSoon(@RequestHeader("factoryId") Long factoryId, @RequestBody FreeSoonMachinePlanRequest request) {
        var query = new FreeSoonMachinePlanQuery();
        query.setCylinderDiameterList(request.getCylinderDiameterList());
        query.setNeedleSpacingList(request.getNeedleSpacingList());
        query.setAreaList(request.getAreaList());
        query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? null : TimeUtil.fromGeneralString(request.getStartTime()));
        query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? null : TimeUtil.fromGeneralString(request.getEndTime()));
        try {
            return new ReturnData<>(machinePlanQueryApplication.queryFreeSoonMachine(query));
        } catch (IllegalArgumentException e) {
            return new ReturnData<>(400, e.getMessage());
        } catch (Exception e) {
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("task/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<MachineTaskListDTO>> pageQueryPlannedTask(@RequestHeader("factoryId") Long factoryId,
                                                                           @RequestBody PageQueryMachineTaskRequest request) {
        var query = PlanConvertor.convertPageMachineTaskRequestToQuery(request);

        try {
            return new ReturnData<>(machinePlanQueryApplication.pageQueryMachineTask(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query TaskPage error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("task/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updatePlannedTask(@RequestBody UpdateMachineTaskRequest request) {
        var command = new UpdateTaskCommand();
        command.setTaskId(request.getTaskId());
        command.setTaskFlag(request.getFlag());
        try {
            planOperateApplication.updateTask(command);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Update Task error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("task/cancel")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> cancelPlannedTask(@RequestBody CancelMachineTaskRequest request) {
        var command = new CancelTaskCommand();
        command.setTaskId(request.getTaskId());
        try {
            planOperateApplication.cancelTask(command);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Cancel Task error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @GetMapping("task/detail")
    @ResponseBody
    @SantoniHeader
    public ReturnData<MachineTaskDetailDTO> exportPlannedTask(@RequestParam("taskId") long taskId) {
        var query = new MachineTaskDetailQuery();
        query.setTaskId(taskId);
        try {
            return new ReturnData<>(machinePlanQueryApplication.queryMachineTaskDetail(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query TaskDetail error, req:{}", taskId, e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/detail")
    @ResponseBody
    @SantoniHeader
    public ReturnData<MachineLevelDetailPlanDTO> queryMachinePlanDetail(@RequestHeader("factoryId") Long factoryId,
                                                                        @RequestBody MachinePlanRequest request) {
        if (null == factoryId || factoryId <= 0) {
            return new ReturnData<>(400, "无权查看");
        }
        try {
            var query = PlanConvertor.convertMachinePlanRequestToQuery(request);
            return new ReturnData<>(machinePlanQueryApplication.machinePlanDetail(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query MachinePlanDetail error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/daily_usage")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<MachineDailyUsageDTO>> queryDailyMachineUsage(@RequestBody MachinePlanRequest request) {
        try {
            var query = PlanConvertor.convertMachinePlanRequestToQuery(request);
            return new ReturnData<>(machinePlanQueryApplication.queryDailyMachineUsage(query));
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query DailyMachineUsage error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }
}
