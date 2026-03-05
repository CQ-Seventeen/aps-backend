package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;

public class StyleDailyProduction {

    private StyleCode styleCode;

    private ProduceQuantity quantity;

    private ProduceQuantity tillTodayQuantity;

    private ProduceDate date;

    private ProduceQuantity actualQuantity;

    private ProduceQuantity actualTDQuantity;

    public StyleDailyProduction(StyleCode styleCode,
                                ProduceDate date,
                                ProduceQuantity quantity,
                                ProduceQuantity tillTodayQuantity,
                                ProduceQuantity actualQuantity,
                                ProduceQuantity actualTDQuantity) {
        this.styleCode = styleCode;
        this.quantity = quantity;
        this.date = date;
        this.actualQuantity = actualQuantity;
        this.tillTodayQuantity = tillTodayQuantity;
        this.actualTDQuantity = actualTDQuantity;
    }

    public StyleDailyProduction accumulateNextDay(ProduceDate date, ProduceQuantity quantity) {
        return new StyleDailyProduction(this.styleCode, date, quantity, this.tillTodayQuantity.plus(quantity),
                null, actualTDQuantity.plus(quantity));

    }

    public void accumulateProduction(ProduceQuantity quantity) {
        this.quantity = this.quantity.plus(quantity);
        this.tillTodayQuantity = this.tillTodayQuantity.plus(quantity);
        if (null != this.actualQuantity) {
            this.actualQuantity = this.actualQuantity.plus(quantity);
            this.actualTDQuantity = this.actualTDQuantity.plus(quantity);
        }
    }

    public static StyleDailyProduction firstProduction(StyleCode styleCode, ProduceDate date, ProduceQuantity quantity) {
        return new StyleDailyProduction(styleCode, date, quantity, quantity, null, quantity);
    }

    public static StyleDailyProduction empty(StyleCode styleCode, ProduceDate date) {
        var zero = ProduceQuantity.zero();
        return new StyleDailyProduction(styleCode, date, zero, zero, null, zero);
    }

    public String getStyleCode() {
        return styleCode.value();
    }

    public int getQuantity() {
        return null == actualQuantity ? quantity.getValue() : actualQuantity.getValue();
    }

    public int getTillTodayQuantity() {
        return null == actualTDQuantity ? tillTodayQuantity.getValue() : actualTDQuantity.getValue();
    }

    public String getDate() {
        return date.value();
    }

    public void modifyQuantity(ProduceQuantity actualQuantity) {
        this.actualQuantity = actualQuantity;
        if (null == actualTDQuantity || actualTDQuantity.isZero()) {
            actualTDQuantity = actualQuantity;
        } else {
            if (actualQuantity.biggerThan(this.quantity)) {
                actualTDQuantity = actualTDQuantity.plus(actualQuantity.minus(this.quantity));
            } else {
                actualTDQuantity = actualTDQuantity.minus(this.quantity.minus(actualQuantity));
            }
        }
    }
}
