package com.santoni.iot.aps.infrastructure.po.assistance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class YarnUsagePO {

    private String yarnId;

    private String yarnCode;

    private String lotNumber;

    private String supplierCode;

    private String twist;

    private String color;

    private BigDecimal percentage;

    private BigDecimal weight;
}
