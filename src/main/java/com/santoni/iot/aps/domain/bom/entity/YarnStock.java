package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.Percentage;
import lombok.Getter;

@Getter
public class YarnStock {

    private Yarn yarn;

    private LotNumber lotNumber;

    private SupplierCode supplierCode;

    private Twist twist;

    private Color color;

    private Weight weight;

    private Percentage percentage;

    public YarnStock(Yarn yarn,
                     LotNumber lotNumber,
                     SupplierCode supplierCode,
                     Twist twist,
                     Color color,
                     Weight weight,
                     Percentage percentage) {
        this.yarn = yarn;
        this.lotNumber = lotNumber;
        this.supplierCode = supplierCode;
        this.twist = twist;
        this.color = color;
        this.weight = weight;
        this.percentage = percentage;
    }
}
