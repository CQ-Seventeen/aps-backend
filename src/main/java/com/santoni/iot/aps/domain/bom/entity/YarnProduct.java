package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import lombok.Getter;

@Getter
public class YarnProduct {

    private Yarn yarn;

    private YarnName yarnName;

    private PackageUnit packageUnit;

    private LotNumber lotNumber;

    private SupplierCode supplierCode;

    private Twist twist;

    private Color color;

    private YarnDesc desc;

    public YarnProduct(Yarn yarn,
                       YarnName yarnName,
                       PackageUnit packageUnit,
                       LotNumber lotNumber,
                       SupplierCode supplierCode,
                       Twist twist,
                       Color color,
                       YarnDesc desc) {
        this.yarn = yarn;
        this.yarnName = yarnName;
        this.packageUnit = packageUnit;
        this.lotNumber = lotNumber;
        this.supplierCode = supplierCode;
        this.twist = twist;
        this.color = color;
        this.desc = desc;
    }
}
