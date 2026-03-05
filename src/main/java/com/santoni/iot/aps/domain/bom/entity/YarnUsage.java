package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.Percentage;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
public class YarnUsage {

    private Yarn yarn;

    private LotNumber lotNumber;

    private SupplierCode supplierCode;

    private Twist twist;

    private Color color;

    private Weight weight;

    private Percentage percentage;

    public YarnUsage(Yarn yarn,
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

    public YarnUsage(String yarnId,
                     String yarnCode,
                     String lotNumber,
                     String supplierCode,
                     String twist,
                     String color,
                     BigDecimal weight,
                     BigDecimal percentage) {
        this.yarn = StringUtils.isBlank(yarnCode) ? null : new Yarn(yarnId, yarnCode);
        this.lotNumber = StringUtils.isBlank(lotNumber) ? null : new LotNumber(lotNumber);
        this.supplierCode = StringUtils.isBlank(supplierCode) ? null : new SupplierCode(supplierCode);
        this.twist = StringUtils.isBlank(twist) ? null : new Twist(twist);
        this.color = StringUtils.isBlank(color) ? null : new Color(color);
        this.weight = null == weight ? null : new Weight(weight);
        this.percentage = null == percentage ? null : new Percentage(percentage);
    }

    public void addWeight(BigDecimal weight) {
        if (null == this.weight) {
            this.weight = new Weight(weight);
        } else {
            this.weight = new Weight(this.weight.value().add(weight));
        }
    }

    public String getYarnKey() {
        return null == yarn ? "" : yarn.code()
                + "-" + (null == lotNumber ? "" : lotNumber.value())
                + "-" + (null == supplierCode ? "" : supplierCode.value())
                + "-" + (null == twist ? "" : twist.value())
                + "-" + (null == color ? "" : color.value());
    }
}
