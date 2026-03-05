package com.santoni.iot.aps.adapter.excel.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.Map;

@Slf4j
public class DataBuilder {

    public static <T> T buildInstance(Class<T> clazz,
                                      XSSFRow row,
                                      Map<String, Integer> fieldNameIndexMap,
                                      DataFormatter formatter) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (var field : clazz.getDeclaredFields()) {
                var annotation = field.getAnnotation(ExcelColName.class);
                if (null != annotation) {
                    Integer index = fieldNameIndexMap.get(annotation.filedName());
                    if (null != index) {
                        var cell = row.getCell(index);

                        if (field.getType() == String.class) {
                            field.set(instance, formatter.formatCellValue(cell));
                        } else if (field.getType() == Integer.class) {
                            field.set(instance, Integer.parseInt(formatter.formatCellValue(cell)));
                        } else if (field.getType() == Double.class) {
                            field.set(instance, Double.parseDouble(formatter.formatCellValue(cell)));
                        } else {
                            throw new IllegalArgumentException("暂不支持解析的Field类型" + field.getType());
                        }
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            log.error("Parse RowData to Instance exception, class:{}, row:{}", clazz, row, e);
            return null;
        }

    }
}
