package com.santoni.iot.aps.adapter.excel.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.adapter.excel.parser.rowData.ProduceOrderRowData;
import com.santoni.iot.aps.adapter.excel.parser.rowData.StyleRowData;
import com.santoni.iot.aps.application.bom.command.BatchCreateStyleCommand;
import com.santoni.iot.aps.application.bom.command.CreateStyleCommand;
import com.santoni.iot.aps.application.bom.command.OperateStyleComponentCommand;
import com.santoni.iot.aps.application.bom.command.OperateStyleSkuCommand;
import com.santoni.iot.aps.application.order.command.BatchCreateProduceOrderCommand;
import com.santoni.iot.aps.application.order.command.CreateProduceOrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelFileParser {

    public BatchCreateStyleCommand parseStyleFromExcel(MultipartFile file) {
        List<StyleRowData> styleRowDataList = parseDataFromExcel(file, StyleRowData.class);

        var styleMap = styleRowDataList.stream()
                .collect(Collectors.groupingBy(it -> it.styleCode,
                        Collectors.groupingBy(it -> it.size)));

        List<CreateStyleCommand> styleLit = Lists.newArrayListWithExpectedSize(styleMap.size());
        for (var entry : styleMap.entrySet()) {
            var cmd = new CreateStyleCommand();
            List<OperateStyleSkuCommand> skuList = Lists.newArrayListWithExpectedSize(entry.getValue().size());
            boolean setStyle = false;
            for (var skuEntry : entry.getValue().entrySet()) {
                var sku = new OperateStyleSkuCommand();
                if (!setStyle) {
                    var firstRow = skuEntry.getValue().get(0);
                    cmd.setCode(firstRow.styleCode);
                    cmd.setName(firstRow.styleName);
                    cmd.setDescription(firstRow.styleDesc);
                    setStyle = true;
                }
                sku.setSize(skuEntry.getKey());
                sku.setComponents(skuEntry.getValue().stream().map(this::convertFromStyleRowData).toList());
                skuList.add(sku);
            }
            cmd.setSkuList(skuList);
            styleLit.add(cmd);
        }
        var res = new BatchCreateStyleCommand();
        res.setStyleList(styleLit);
        return res;
    }

    private OperateStyleComponentCommand convertFromStyleRowData(StyleRowData data) {
        var component = new OperateStyleComponentCommand();
        component.setPart(data.part);
        component.setCylinderDiameter(data.cylinderDiameter);
        component.setNeedleSpacing(data.needleSpacing);
        component.setType(data.type);
        component.setProgramFileName(data.programFileName);
        component.setNumber(data.number);
        component.setRatio(data.ratio);
        component.setExpectedProduceTime(data.expectedTime);
        component.setExpectedWeight(data.expectedWeight);
        component.setStandardNumber(data.standardNumber);
        component.setDescription(data.partDesc);
        return component;
    }


    public BatchCreateProduceOrderCommand parseProduceOrderFromExcel(MultipartFile file) {
        List<ProduceOrderRowData> orderRowDataList = parseDataFromExcel(file, ProduceOrderRowData.class);

        var orderMap = orderRowDataList.stream()
                .collect(Collectors.groupingBy(it -> it.orderCode));

        List<CreateProduceOrderCommand> orderList = Lists.newArrayListWithExpectedSize(orderMap.size());
        for (var entry : orderMap.entrySet()) {
            var cmd = new CreateProduceOrderCommand();
        }
        return null;
    }

    private <T> List<T> parseDataFromExcel(MultipartFile file, Class<T> clazz) {
        List<T> res = Lists.newArrayList();
        try (var inputStream = file.getInputStream()) {
            var workbook = new XSSFWorkbook(inputStream);
            var sheet = workbook.getSheetAt(0);

            var formatter = new DataFormatter();
            Map<String, Integer> filedIndexMap = Maps.newHashMap();
            var header = sheet.getRow(0);
            for (var cell : header) {
                filedIndexMap.put(formatter.formatCellValue(cell), cell.getColumnIndex());
            }

            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                var rowData = sheet.getRow(row);
                if (null == rowData) {
                    continue;
                }
                var data = DataBuilder.buildInstance(clazz, rowData, filedIndexMap, formatter);
                if (null != data) {
                    res.add(data);
                }
            }
            return res;
        } catch (IOException ioe) {
            log.error("Read excel file IOException", ioe);
            return List.of();
        } catch (Exception e) {
            log.error("Read excel file, parse data exception", e);
            return List.of();
        }
    }
}
