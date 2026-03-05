package com.santoni.iot.aps.adapter.execute.request;

import lombok.Data;

@Data
public class ReportWeaveProductRequest {

    private String manufactureOrder;

    private String styleCode;

    private String machineCode;

    private String symbol;

    private String part;

    private String color;

    private String size;

    private String barcode;

    private String workDate;

    private String shift;

    private Integer process;

    private String figure;

    private int qualifiedQuantity;

    private int defectQuantity;
}
