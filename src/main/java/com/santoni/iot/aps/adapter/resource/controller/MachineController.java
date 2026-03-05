package com.santoni.iot.aps.adapter.resource.controller;

import com.santoni.iot.aps.adapter.resource.convertor.MachineConvertor;
import com.santoni.iot.aps.adapter.resource.request.CreateMachineRequest;
import com.santoni.iot.aps.adapter.resource.request.OperateMachineAttrConfigRequest;
import com.santoni.iot.aps.adapter.resource.request.PageQueryMachineRequest;
import com.santoni.iot.aps.adapter.resource.request.UpdateMachineRequest;
import com.santoni.iot.aps.application.resource.ResourceOperateApplication;
import com.santoni.iot.aps.application.resource.ResourceQueryApplication;
import com.santoni.iot.aps.application.resource.dto.MachineAttrConfigDTO;
import com.santoni.iot.aps.application.resource.dto.MachineListDTO;
import com.santoni.iot.aps.application.resource.dto.MachineOptionsDTO;
import com.santoni.iot.aps.application.resource.dto.OverviewMachineDTO;
import com.santoni.iot.aps.application.resource.query.OverviewMachineQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private ResourceQueryApplication resourceQueryApplication;

    @Autowired
    private ResourceOperateApplication resourceOperateApplication;

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<MachineListDTO>> pageQueryMachine(@RequestHeader("instituteId") long instituteId,
                                                                   @RequestBody PageQueryMachineRequest request) {
        var query = MachineConvertor.convertPageMachineRequestToQuery(request);
        return new ReturnData<>(resourceQueryApplication.pageMachine(query));
    }

    @PostMapping("/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createMachine(@RequestHeader("instituteId") long instituteId,
                                          @RequestBody CreateMachineRequest request) {
        var cmd = MachineConvertor.convertCreateMachineReqToCmd(request, instituteId);
        try {
            resourceOperateApplication.createMachine(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Create Machine error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Create Machine error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateMachine(@RequestHeader("instituteId") long instituteId,
                                          @RequestBody UpdateMachineRequest request) {
        var cmd = MachineConvertor.convertUpdateMachineReqToCmd(request, instituteId);
        try {
            resourceOperateApplication.updateMachine(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Update Machine error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Update Machine error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @GetMapping("/overview")
    @ResponseBody
    @SantoniHeader
    public ReturnData<OverviewMachineDTO> overviewMachine(@RequestHeader("instituteId") long instituteId) {
        var today = TimeUtil.getStartOfToday();
        var dayPastTen = TimeUtil.getEndOf(today.plusDays(9));
        try {
            var query = new OverviewMachineQuery();
            query.setStartTime(today);
            query.setEndTime(dayPastTen);
            return new ReturnData<>(resourceQueryApplication.overviewMachine(query));
        } catch (Exception e) {
            log.error("OverviewMachine exception", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @GetMapping("/options")
    @ResponseBody
    @SantoniHeader
    public ReturnData<MachineOptionsDTO> getOptions() {
        return new ReturnData<>(resourceQueryApplication.getMachineOptions());
    }

    @GetMapping("/attr/list_config")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<MachineAttrConfigDTO>> getAllAttrConfig() {
        return new ReturnData<>(resourceQueryApplication.getAllAttrConfig());
    }

    @PostMapping("/attr/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createMachineAttr(@RequestHeader("instituteId") long instituteId,
                                              @RequestBody OperateMachineAttrConfigRequest request) {
        try {
            var cmd = MachineConvertor.convertOperateAttrConfigToCmd(request);
            resourceOperateApplication.createMachineAttr(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Create MachineAttr error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Create MachineAttr error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/attr/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateMachineAttr(@RequestHeader("instituteId") long instituteId,
                                              @RequestBody OperateMachineAttrConfigRequest request) {
        try {
            var cmd = MachineConvertor.convertOperateAttrConfigToCmd(request);
            resourceOperateApplication.updateMachineAttr(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Update MachineAttr error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Update MachineAttr error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }
}
