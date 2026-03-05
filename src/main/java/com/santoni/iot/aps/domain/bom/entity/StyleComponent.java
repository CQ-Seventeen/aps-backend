package com.santoni.iot.aps.domain.bom.entity;

import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.bom.constant.ComponentType;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Number;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.santoni.iot.aps.domain.plan.constant.PlanConstant.DEFAULT_PRODUCE_EFFICIENCY;

@Getter
public class StyleComponent {

    private ComponentId componentId;

    private SkuCode skuCode;

    private ProduceOrderCode produceOrderCode;

    private Part part;

    private Color color;

    private ComponentType type;

    private ProgramFile program;

    private MachineSize machineSize;

    private ExpectedProduceTime expectedProduceTime;

    private MachineRequirement requirement;

    private Number number;

    private ComponentRatio ratio;

    private ExpectedWeight expectedWeight;

    private StandardNumber standardNumber;

    private StyleDesc styleDesc;

    private List<YarnUsage> yarnUsages;

    private ProduceEfficiency produceEfficiency;

    private Quantity dailyTheoreticalQuantity;

    private FinishWidth finishWidth;

    private FinishLength finishLength;

    private WasteRate wasteRate;

    private Dye dye;

    public StyleComponent(ComponentId componentId,
                          SkuCode skuCode,
                          ProduceOrderCode produceOrderCode,
                          Part part,
                          Color color,
                          ComponentType type,
                          ProgramFile program,
                          MachineSize machineSize,
                          ExpectedProduceTime expectedProduceTime,
                          MachineRequirement requirement,
                          Number number,
                          ComponentRatio ratio,
                          ExpectedWeight expectedWeight,
                          StandardNumber standardNumber,
                          StyleDesc styleDesc,
                          List<YarnUsage> yarnUsages,
                          ProduceEfficiency produceEfficiency,
                          FinishWidth finishWidth,
                          FinishLength finishLength,
                          WasteRate wasteRate,
                          Dye dye) {
        this.componentId = componentId;
        this.skuCode = skuCode;
        this.produceOrderCode = produceOrderCode;
        this.part = part;
        this.color = color;
        this.type = type;
        this.program = program;
        this.machineSize = machineSize;
        this.expectedProduceTime = expectedProduceTime;
        this.requirement = requirement;
        this.number = number;
        this.ratio = ratio;
        this.expectedWeight = expectedWeight;
        this.standardNumber = standardNumber;
        this.styleDesc = styleDesc;
        this.yarnUsages = yarnUsages;
        this.produceEfficiency = produceEfficiency;
        this.finishWidth = finishWidth;
        this.finishLength = finishLength;
        this.wasteRate = wasteRate;
        this.dye = dye;
    }

    public double totalSeconds(Quantity quantity) {
        return (quantity.getValue() * number.value() * expectedProduceTime.value()) / ratio.value();
    }

    public double actualSeconds(Quantity actualPieces) {
        return actualPieces.getValue() * expectedProduceTime.value();
    }

    public double onePieceSeconds() {
        return (number.value() * expectedProduceTime.value()) / ratio.value();
    }

    public boolean isPrincipal() {
        return type == ComponentType.PRINCIPAL;
    }

    public int actualPieces(Quantity quantity) {
        return (int) Math.floor((double) (quantity.getValue() * number.value()) / ratio.value());
    }

    public void calculateYarnDemand(Quantity quantity) {
        Map<String, BigDecimal> demandMap = Maps.newHashMap();

        for (var consumption : yarnUsages) {
            var curDemand = demandMap.get(consumption.getYarn().code());
            if (null != curDemand) {
                curDemand = curDemand.add(consumption.getWeight().value().multiply(BigDecimal.valueOf(quantity.getValue())));
                demandMap.put(consumption.getYarn().code(), curDemand);
            } else {

            }
        }
    }

    public BigDecimal getEfficiency() {
        if (null == produceEfficiency) {
            return DEFAULT_PRODUCE_EFFICIENCY;
        }
        return produceEfficiency.getEfficiency();
    }

    public Quantity getDailyTheoreticalQuantity() {
        if (null == dailyTheoreticalQuantity) {
            if (null == expectedProduceTime) {
                this.dailyTheoreticalQuantity = Quantity.zero();
                return dailyTheoreticalQuantity;
            }
            var quantity = (getEfficiency().multiply(PlanConstant.SECONDS_ONE_DAY)).divide(BigDecimal.valueOf(expectedProduceTime.value()), RoundingMode.HALF_UP);
            this.dailyTheoreticalQuantity = Quantity.of(quantity.intValue());
            return dailyTheoreticalQuantity;
        }
        return dailyTheoreticalQuantity;
    }

    public BigDecimal theoreticalDays(Quantity quantity) {
        return BigDecimal.valueOf(expectedProduceTime.value()).multiply(BigDecimal.valueOf(quantity.getValue())).divide(getEfficiency().multiply(PlanConstant.SECONDS_ONE_DAY), 4, RoundingMode.HALF_UP);
    }

    public String getUniqueKey() {
        return null == part ? "" : part.value();
    }

    public long theoreticalSeconds(Quantity quantity) {
        return BigDecimal.valueOf(expectedProduceTime.value()).multiply(BigDecimal.valueOf(quantity.getValue())).divide(getEfficiency(), 0, RoundingMode.CEILING).longValue();
    }
}
