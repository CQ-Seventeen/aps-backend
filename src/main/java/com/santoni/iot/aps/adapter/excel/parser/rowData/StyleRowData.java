package com.santoni.iot.aps.adapter.excel.parser.rowData;

import com.santoni.iot.aps.adapter.excel.parser.ExcelColName;

public class StyleRowData {

    @ExcelColName(filedName = "款式编号")
    public String styleCode;

    @ExcelColName(filedName = "款式名称")
    public String styleName;

    @ExcelColName(filedName = "描述")
    public String styleDesc;

    @ExcelColName(filedName = "尺码")
    public String size;

    @ExcelColName(filedName = "部位")
    public String part;

    @ExcelColName(filedName = "类型")
    public Integer type;

    @ExcelColName(filedName = "配比")
    public Integer number;

    @ExcelColName(filedName = "每筒数量")
    public Integer ratio;

    @ExcelColName(filedName = "筒径")
    public Integer cylinderDiameter;

    @ExcelColName(filedName = "针距")
    public Integer needleSpacing;

    @ExcelColName(filedName = "下机时间")
    public Double expectedTime;

    @ExcelColName(filedName = "程序名")
    public String programFileName;

    @ExcelColName(filedName = "下机克重")
    public Double expectedWeight;

    @ExcelColName(filedName = "标准框数")
    public Integer standardNumber;

    @ExcelColName(filedName = "部位描述")
    public String partDesc;

    public StyleRowData() {
    }

}
