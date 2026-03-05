package com.santoni.iot.aps.adapter.excel.builder;

import com.santoni.iot.aps.application.plan.dto.machine.MachineTaskListDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelFileBuilder {

    public byte[] writeMachinePlanToExcel(List<MachineTaskListDTO> machineTaskList) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet1");

        var header = sheet.createRow(0);
        header.createCell(0).setCellValue("订单号");
        header.createCell(1).setCellValue("机台号");
        header.createCell(2).setCellValue("款号");
        header.createCell(3).setCellValue("尺码");
        header.createCell(4).setCellValue("部位");
        header.createCell(5).setCellValue("寸数");
        header.createCell(6).setCellValue("计划数量");

        for (int i = 0; i < machineTaskList.size(); i++) {
            var task = machineTaskList.get(i);
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(task.getProduceOrderCode());
            row.createCell(1).setCellValue(task.getDeviceId());
            row.createCell(2).setCellValue(task.getStyleCode());
            row.createCell(3).setCellValue(task.getSize());
            row.createCell(4).setCellValue(task.getPart());
            row.createCell(5).setCellValue(task.getCylinderDiameter());
            row.createCell(6).setCellValue(task.getPlannedQuantity());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
