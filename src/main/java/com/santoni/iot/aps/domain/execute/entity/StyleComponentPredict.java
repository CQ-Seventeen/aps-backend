package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Weight;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class StyleComponentPredict {

    private ProduceOrderCode produceOrderCode;

    private StyleComponent component;

    private ProduceQuantity dailyQuantity;

    private ProduceQuantity predictNextQuantity;

    private List<PlannedTask> arrangedTasks;

    private List<YarnUsage> totalYarnUsage;

    private StyleComponentPredict(ProduceOrderCode produceOrderCode,
                                  StyleComponent component,
                                  ProduceQuantity dailyQuantity,
                                  ProduceQuantity predictNextQuantity,
                                  List<PlannedTask> arrangedTasks,
                                  List<YarnUsage> totalYarnUsage) {
        this.produceOrderCode = produceOrderCode;
        this.component = component;
        this.dailyQuantity = dailyQuantity;
        this.predictNextQuantity = predictNextQuantity;
        this.arrangedTasks = arrangedTasks;
        this.totalYarnUsage = totalYarnUsage;
    }

    public static StyleComponentPredict init(ProduceOrderCode orderCode, StyleComponent styleComponent, ProduceQuantity dailyQuantity) {
        return new StyleComponentPredict(orderCode, styleComponent, dailyQuantity, ProduceQuantity.zero(), Lists.newArrayList(), null);
    }


    public void addProduceQuantity(ProduceQuantity quantity) {
        this.dailyQuantity = this.dailyQuantity.plus(quantity);
    }

    public void addPredictNextQuantity(ProduceQuantity predictNextQuantity, PlannedTask plannedTask) {
        this.predictNextQuantity = this.predictNextQuantity.plus(predictNextQuantity);
        this.arrangedTasks.add(plannedTask);
    }

    public void calculateYarnUsage() {
        if (this.predictNextQuantity.isZero() || CollectionUtils.isEmpty(component.getYarnUsages())) {
            return;
        }
        if (null != totalYarnUsage) {
            return;
        }
        List<YarnUsage> res = Lists.newArrayList();
        for (var usage : component.getYarnUsages()) {
            res.add(new YarnUsage(usage.getYarn(), usage.getLotNumber(), usage.getSupplierCode(),
                    usage.getTwist(), usage.getColor(),
                    new Weight(usage.getWeight().value().multiply(BigDecimal.valueOf(predictNextQuantity.getValue()))), usage.getPercentage()));
        }
        this.totalYarnUsage = res;
    }
}
