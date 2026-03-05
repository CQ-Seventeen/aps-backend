package com.santoni.iot.aps.adapter.execute.controller;

import com.santoni.iot.aps.adapter.execute.request.ManualWriteBackProductionRequest;
import com.santoni.iot.aps.adapter.execute.request.OrderStyleProductionRequest;
import com.santoni.iot.aps.adapter.execute.request.ProductionDailyTrackRequest;
import com.santoni.iot.aps.adapter.execute.request.ReportWeaveProductRequest;
import com.santoni.iot.aps.application.execute.ProduceOperateApplication;
import com.santoni.iot.aps.application.execute.ProduceQueryApplication;
import com.santoni.iot.aps.application.execute.command.RecordMachineProductionCommand;
import com.santoni.iot.aps.application.execute.command.WriteProductionToTaskCommand;
import com.santoni.iot.aps.application.execute.dto.OrderStyleProductionDTO;
import com.santoni.iot.aps.application.execute.dto.ProductionAggregateByMachineDTO;
import com.santoni.iot.aps.application.execute.dto.StyleComponentProductionDTO;
import com.santoni.iot.aps.application.execute.query.FactoryProductionTrackQuery;
import com.santoni.iot.aps.application.execute.query.OrderStyleProductionQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import com.santoni.iot.utils.record.constant.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/produce")
@RestController
@Slf4j
public class ProduceController {

    @Autowired
    private ProduceQueryApplication produceQueryApplication;

    @Autowired
    private ProduceOperateApplication produceOperateApplication;

    @PostMapping("/production/track")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<StyleComponentProductionDTO>> queryProductionTrack(@RequestHeader("factoryId") Long factoryId,
                                                                              @RequestBody ProductionDailyTrackRequest request) {
        var query = new FactoryProductionTrackQuery();
        if (null != factoryId && factoryId > 0) {
            query.setFactoryId(factoryId);
        } else {
            query.setFactoryId(request.getFactoryId());
        }
        query.setDate(request.getDate());
        try {
            return new ReturnData<>(produceQueryApplication.queryDailyProductionTrack(query));
        } catch (Exception e) {
            log.error("Query daily production track error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/production/order_style")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<OrderStyleProductionDTO>> queryOrderStyleProduction(@RequestBody OrderStyleProductionRequest request) {
        try {
            var query = new OrderStyleProductionQuery();
            query.setFactoryId(request.getFactoryId());
            return new ReturnData<>(produceQueryApplication.queryOrderStyleProduction(query));
        } catch (Exception e) {
            log.error("Query orderStyle Production track error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/aggregate_by_machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ProductionAggregateByMachineDTO>> productionAggregateByMachine() {
        try {
            return new ReturnData<>(produceQueryApplication.queryProductionAggregateByMachine());
        } catch (Exception e) {
            log.error("Query orderStyle Production track error", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/manual_write_back")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> manualWriteBackProduction(@RequestBody ManualWriteBackProductionRequest request) {
        var command = new WriteProductionToTaskCommand();
        command.setDate(request.getDate());
        try {
            produceOperateApplication.writeProductionToTask(command);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Manual WriteBackProduction error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Manual WriteBackProduction MachineProduction error, req:{}", JacksonUtil.toJson(command), e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/manual_sum_production")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> manualSummaryProduction() {
        try {
            produceOperateApplication.sumProduction();
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("Manual SummaryProduction MachineProduction error, ", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/weave_yield/sync")
    @ResponseBody
    @SantoniHeader(Header.NONE)
    public ReturnData<Void> reportWeaveProduction(@RequestBody ReportWeaveProductRequest request) {
        log.info("Receive Weave production request:{}", JacksonUtil.toJson(request));
        var cmd = convertReportReqToCmd(request);
        try {
            produceOperateApplication.reportProduction(cmd);
            return new ReturnData<>();
        } catch (IllegalArgumentException iae) {
            log.error("Report Production error, bad req:{}", JacksonUtil.toJson(request));
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Report Production error, ", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    private RecordMachineProductionCommand convertReportReqToCmd(ReportWeaveProductRequest request) {
        var cmd = new RecordMachineProductionCommand();
        cmd.setOrderCode(request.getManufactureOrder());
        cmd.setStyleCode(request.getStyleCode());
        cmd.setSize(request.getSize());
        cmd.setPart(request.getPart());
        cmd.setDeviceId(request.getMachineCode());
        cmd.setDate(request.getWorkDate());
        cmd.setPieces(request.getQualifiedQuantity());
        cmd.setDefectPieces(request.getDefectQuantity());
        cmd.setBarCode(request.getBarcode());
        cmd.setType(request.getProcess());
        return cmd;
    }
}
