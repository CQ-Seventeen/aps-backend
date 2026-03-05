package com.santoni.iot.aps.adapter.excel.controller;

import com.santoni.iot.aps.adapter.excel.builder.ExcelFileBuilder;
import com.santoni.iot.aps.adapter.excel.parser.ExcelFileParser;
import com.santoni.iot.aps.adapter.plan.convertor.PlanConvertor;
import com.santoni.iot.aps.adapter.plan.request.machine.PageQueryMachineTaskRequest;
import com.santoni.iot.aps.application.bom.StyleApplication;
import com.santoni.iot.aps.application.order.OrderOperateApplication;
import com.santoni.iot.aps.application.plan.MachinePlanQueryApplication;
import com.santoni.iot.aps.application.resource.ResourceOperateApplication;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;
import com.santoni.iot.aps.application.transfer.DataTransferApplication;
import com.santoni.iot.aps.application.transfer.dto.ExcelTemplateDTO;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/excel")
@RestController
@Slf4j
public class ExcelController {

    @Autowired
    private ExcelFileParser excelFileParser;

    @Autowired
    private ExcelFileBuilder excelFileBuilder;

    @Autowired
    private DataTransferApplication dataTransferApplication;

    @Autowired
    private StyleApplication styleApplication;

    @Autowired
    private OrderOperateApplication orderOperateApplication;

    @Autowired
    private ResourceOperateApplication resourceOperateApplication;

    @Autowired
    private MachinePlanQueryApplication machinePlanQueryApplication;

    @GetMapping("/list_all_template")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<ExcelTemplateDTO>> getImportTemplate() {
        try {
            return new ReturnData<>(dataTransferApplication.listAllExcelTemplate());
        } catch (Exception e) {
            log.error("Get all ExcelTemplate exception", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/import/style")
    @ResponseBody
    @SantoniHeader
    public ReturnData<String> importStyle(@RequestPart("file") MultipartFile file) {
        try {
            var cmd = excelFileParser.parseStyleFromExcel(file);
            if (CollectionUtils.isNotEmpty(cmd.getStyleList())) {
                var res = styleApplication.batchCreateStyle(cmd);
                return new ReturnData<>(buildImportResult(res, "款式"));
            } else {
                return new ReturnData<>(400, "未解析出款式数据");
            }
        } catch (IllegalArgumentException iae) {
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Import style exception", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    private String buildImportResult(BatchOperateResultDTO dto, String docName) {
        var basicInfo = "共成功导入" + dto.getSuccessNumber() + "个" + docName + ",失败" + dto.getFailNumber() + "条\n";
        if (CollectionUtils.isNotEmpty(dto.getFailCodeList())) {
            basicInfo += "以下编号的数据导入失败:" + String.join(",", dto.getFailCodeList());
        }
        return basicInfo;
    }

    @PostMapping("/import/produce_order")
    @ResponseBody
    @SantoniHeader
    public ReturnData<String> importProduceOrder(@RequestPart("file") MultipartFile file) {
        try {
            var cmd = excelFileParser.parseProduceOrderFromExcel(file);
            if (CollectionUtils.isNotEmpty(cmd.getOrderList())) {
                var res = orderOperateApplication.batchCreateProduceOrder(cmd);
                return new ReturnData<>(buildImportResult(res, "生产单"));
            } else {
                return new ReturnData<>(400, "未解析出生产单数据");
            }
        } catch (Exception e) {
            log.error("Import style exception", e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @PostMapping("/import/machine")
    @ResponseBody
    @SantoniHeader
    public ReturnData<String> importMachine(@RequestPart("file") MultipartFile file) {

        return null;
    }

    @PostMapping("/export/machine_plan")
    @SantoniHeader
    public void exportMachinePlan(@RequestHeader("factoryId") Long factoryId,
                                  @RequestBody PageQueryMachineTaskRequest request, HttpServletResponse response) {
        var query = PlanConvertor.convertPageMachineTaskRequestToQuery(request);
        query.setPageSize(-1);
        if (null != factoryId && factoryId > 0) {
            query.setFactoryId(factoryId);
        }
        try (var out = response.getOutputStream()) {
            var data = machinePlanQueryApplication.pageQueryMachineTask(query);
            byte[] excelBytes = excelFileBuilder.writeMachinePlanToExcel(data.getData());

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=机台计划单.xlsx");

            // 将文件写入响应流
            out.write(excelBytes);
            out.flush();

        } catch (IllegalArgumentException iae) {
            log.error("Export Machine Plan, bad request:{}", JacksonUtil.toJson(request), iae);
        } catch (IOException e) {
            log.error("Export Machine Plan, IOException", e);
        }
    }
}
