package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineNumber;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
public class WeaveProduceTrack {

    private WeavingPartOrder weavingPartOrder;

    private StyleComponent component;

    private Quantity planQuantity;

    private ProduceQuantity tillTodayQuantity;

    private ProduceQuantity leftQuantity;

    private MachineNumber machineNumber;

    private List<PlannedTask> arrangedTasks;

    public WeaveProduceTrack(StyleComponent component,
                             WeavingPartOrder weavingPartOrder,
                             Quantity planQuantity,
                             ProduceQuantity tillTodayQuantity,
                             ProduceQuantity leftQuantity,
                             MachineNumber machineNumber) {
        this.component = component;
        this.weavingPartOrder = weavingPartOrder;
        this.planQuantity = planQuantity;
        this.tillTodayQuantity = tillTodayQuantity;
        this.leftQuantity = leftQuantity;
        this.machineNumber = machineNumber;
    }

    public static WeaveProduceTrack of(StyleComponent component, WeavingPartOrder weavingPartOrder, Quantity planQuantity) {
        return new WeaveProduceTrack(component, weavingPartOrder, planQuantity, ProduceQuantity.zero(), ProduceQuantity.of(planQuantity.getValue()), MachineNumber.zero());
    }

    public void setProduceQuantity(ProduceQuantity produceQuantity) {
        this.tillTodayQuantity = produceQuantity;
        if (null != produceQuantity) {
            this.leftQuantity = leftQuantity.minus(produceQuantity);
        }
    }

    public void collectPlannedTasks(List<PlannedTask> plannedTasks) {
        arrangedTasks = plannedTasks;
        if (CollectionUtils.isNotEmpty(plannedTasks)) {
            this.machineNumber = new MachineNumber(plannedTasks.size());
        }
    }

    public double calculateLeftDays() {
        int actualNum = machineNumber.value();
        if (machineNumber.isZero()) {
            actualNum = 1;
        }
        if (component.getDailyTheoreticalQuantity().isZero()) {
            return 0;
        }
        double days = (double) leftQuantity.getValue() / (component.getDailyTheoreticalQuantity().getValue() * actualNum);
        return BigDecimal.valueOf(days)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
