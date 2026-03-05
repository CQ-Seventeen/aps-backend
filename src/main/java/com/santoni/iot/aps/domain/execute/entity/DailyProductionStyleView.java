package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DailyProductionStyleView {

    @Getter
    private List<StyleDailyProduction> styleProductionDetail;

    private ProduceQuantity totalQuantity;

    private Quantity styleNumber;

    public DailyProductionStyleView(List<StyleDailyProduction> styleProductionDetail) {
        this.styleProductionDetail = styleProductionDetail;
        this.styleNumber = Quantity.of(styleProductionDetail.size());
        int quantity = 0;
        if (CollectionUtils.isNotEmpty(styleProductionDetail)) {
            for (var styleProduction : styleProductionDetail) {
                quantity += styleProduction.getQuantity();
            }
        }
        this.totalQuantity = ProduceQuantity.of(quantity);
    }

    public int getTotalQuantity() {
        return totalQuantity.getValue();
    }

    public int getStyleNumber() {
        return styleNumber.getValue();
    }
}
