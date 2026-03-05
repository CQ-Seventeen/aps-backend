package com.santoni.iot.aps.adapter.excel.parser.rowData;

import com.santoni.iot.aps.adapter.excel.parser.ExcelColName;

public class ProduceOrderRowData {

    @ExcelColName(filedName = "订单号")
    public String orderCode;

    @ExcelColName(filedName = "客户编号")
    public String customerCode;

    @ExcelColName(filedName = "交付日期")
    public String deliveryDate;

    @ExcelColName(filedName = "款式编号")
    public String styleCode;
    
    @ExcelColName(filedName = "尺码")
    public String size;

    @ExcelColName(filedName = "订单数")
    public Integer orderQuantity;

    @ExcelColName(filedName = "织造数")
    public Integer weaveQuantity;

    @ExcelColName(filedName = "打样数")
    public Integer sampleQuantity;

    @ExcelColName(filedName = "颜色")
    public String color;

    public ProduceOrderRowData() {
    }
}
