package com.santoni.iot.aps.domain.execute.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Weight;
import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.*;
import com.santoni.iot.aps.domain.execute.entity.valueobj.AlteredQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ToOperateRecords;
import com.santoni.iot.aps.domain.execute.service.ExecuteDomainService;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExecuteDomainServiceImpl implements ExecuteDomainService {

    @Override
    public ToOperateRecords handleRecordsUpdate(List<MachineDailyProduction> newRecords, List<MachineDailyProduction> existRecords) {
        if (CollectionUtils.isEmpty(existRecords)) {
            return new ToOperateRecords(newRecords, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        }
        if (CollectionUtils.isEmpty(newRecords)) {
            return new ToOperateRecords(Collections.emptyList(), Collections.emptyList(), existRecords, Collections.emptyMap());
        }
        var existingMap = existRecords.stream()
                .collect(Collectors.toMap(MachineDailyProduction::getUniqueKey, it -> it));

        List<MachineDailyProduction> toInsertList = Lists.newArrayList();
        List<MachineDailyProduction> toUpdateList = Lists.newArrayList();
        Map<Long, AlteredQuantity> alterByUpdate = Maps.newHashMap();
        List<MachineDailyProduction> toDeleteList = Collections.emptyList();

        for (var record : newRecords) {
            var exist = existingMap.get(record.getUniqueKey());
            if (null == exist) {
                toInsertList.add(record);
            } else {
                var alter = exist.changeQuantity(record.getQuantity());
                toUpdateList.add(exist);
                alterByUpdate.put(exist.getRecordId().value(), alter);
                existingMap.remove(exist.getUniqueKey());
            }
        }
        if (MapUtils.isNotEmpty(existingMap)) {
            toDeleteList = existingMap.values().stream().toList();
        }
        return new ToOperateRecords(toInsertList, toUpdateList, toDeleteList, alterByUpdate);
    }

    @Override
    public List<StyleComponentPredict> predictDailyProduction(List<MachineDailyProduction> prevProduction,
                                                              List<PlannedTask> curTasks,
                                                              Map<String, Map<String, StyleComponent>> componentMap,
                                                              LocalDateTime startTime,
                                                              LocalDateTime endTime) {
        if (CollectionUtils.isEmpty(curTasks) && CollectionUtils.isEmpty(prevProduction)) {
            return Collections.emptyList();
        }

        Map<String, StyleComponentPredict> predictMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(prevProduction)) {
            for (MachineDailyProduction prod : prevProduction) {
                var component = componentMap.getOrDefault(prod.getSkuCode().value(), Collections.emptyMap()).get(prod.getPart().value());
                if (null == component) {
                    continue;
                }

                var key = buildKey(prod.getOrderCode(), prod.getSkuCode(), prod.getPart());
                var predict = predictMap.get(key);
                if (predict == null) {
                    predict = StyleComponentPredict.init(prod.getOrderCode(), component, ProduceQuantity.of(prod.getQuantity().getValue()));
                    predictMap.put(key, predict);
                } else {
                    predict.addProduceQuantity(prod.getQuantity());
                }
            }
        }
        if (CollectionUtils.isEmpty(curTasks)) {
            return Lists.newArrayList(predictMap.values());
        }

        if (CollectionUtils.isNotEmpty(curTasks)) {
            for (var task : curTasks) {
                var key = buildKey(task.getProduceOrderCode(), task.getSkuCode(), task.getPart());

                var predict = predictMap.get(key);
                if (null == predict) {
                    var component = componentMap.getOrDefault(task.getSkuCode().value(), Collections.emptyMap()).get(task.getPart().value());
                    if (null == component) {
                        continue;
                    }
                    predict = StyleComponentPredict.init(task.getProduceOrderCode(), component, ProduceQuantity.zero());
                    var ratio = task.occupyPercent(startTime, endTime);
                    predict.addPredictNextQuantity(ProduceQuantity.of(ratio.multiply(BigDecimal.valueOf(component.getDailyTheoreticalQuantity().getValue())).intValue()), task);
                    predictMap.put(key, predict);
                } else {
                    var ratio = task.occupyPercent(startTime, endTime);
                    predict.addPredictNextQuantity(ProduceQuantity.of(ratio.multiply(BigDecimal.valueOf(predict.getComponent().getDailyTheoreticalQuantity().getValue())).intValue()), task);
                }
            }
        }

        // 第三步：对于每个 StyleComponentPredict 调用 calculateYarnUsage 方法
        List<StyleComponentPredict> result = Lists.newArrayList();
        for (StyleComponentPredict predict : predictMap.values()) {
            predict.calculateYarnUsage();
            result.add(predict);
        }

        return result;
    }

    @Override
    public List<YarnUsage> gatherYarnUsage(List<StyleComponentPredict> predicts) {
        Map<String, YarnUsage> yarnUsageMap = Maps.newHashMap();
        for (var predict : predicts) {
            for (var usage : predict.getTotalYarnUsage()) {
                var key = usage.getYarnKey();
                var existUsage = yarnUsageMap.get(key);
                if (null == existUsage) {
                    yarnUsageMap.put(key, new YarnUsage(usage.getYarn(), usage.getLotNumber(), usage.getSupplierCode(),
                            usage.getTwist(), usage.getColor(),
                            usage.getWeight(), usage.getPercentage()));
                } else {
                    existUsage.addWeight(usage.getWeight().value());
                }
            }
        }
        return Lists.newArrayList(yarnUsageMap.values());
    }

    @Override
    public void collectProductionByOrder(MachineAggregateTable table,
                                         List<WeavingPartOrder> partOrders,
                                         List<ProductionSum> productionSum,
                                         List<StyleSku> skuList) {
        var sumMap = productionSum.stream().collect(Collectors.toMap(it -> it.getKey().value(), it -> it));
        var componentMap = skuList.stream().collect(Collectors.toMap(it -> it.getCode().value(), StyleSku::getComponentMap));

        for (var partOrder : partOrders) {
            var component = componentMap.getOrDefault(partOrder.getDemand().getSkuCode().value(), Map.of()).get(partOrder.getDemand().getPart().value());
            if (null == component) {
                continue;
            }
            var aggregate = table.find(component.getMachineSize().getCylinderDiameter(), component.getMachineSize().getNeedleSpacing());
            if (null == aggregate) {
                continue;
            }
            var sum = sumMap.get(partOrder.getSumKey());
            aggregate.collectPartOrder(partOrder, sum, component);
        }
    }

    @Override
    public List<ProductionSum> summaryDayProduction(List<MachineDailyProduction> curProduction,
                                                    List<ProductionSum> prevDaySum,
                                                    List<ProductionSum> latestSum,
                                                    List<ProductionSum> curExistSum) {
        var productionMap = curProduction.stream().collect(Collectors.groupingBy(MachineDailyProduction::getSumKey));
        var existSumMap = curExistSum.stream().collect(Collectors.toMap(it -> it.getKey().value(), it -> it));
        var prevSumMap = prevDaySum.stream().collect(Collectors.toMap(it -> it.getKey().value(), it -> it));
        var latestSumMap = latestSum.stream().collect(Collectors.toMap(it -> it.getKey().value(), it -> it));

        List<ProductionSum> result = Lists.newArrayListWithExpectedSize(productionMap.size());
        for (var curEntry : productionMap.entrySet()) {
            var quantity = summaryQuantity(curEntry.getValue());
            var existSum = existSumMap.get(curEntry.getKey());
            if (null != existSum) {
                existSum.modifyQuantity(quantity.getLeft(), quantity.getRight());
                result.add(existSum);
                continue;
            }
            var prevSum = prevSumMap.get(curEntry.getKey());
            if (prevSum != null) {
                var curSum = prevSum.accumulateNextDay(curEntry.getValue().get(0).getDate(), quantity.getLeft(), quantity.getRight());
                result.add(curSum);
                continue;
            }
            var latest = latestSumMap.get(curEntry.getKey());
            if (null != latest) {
                var curSum = latest.accumulateNextDay(curEntry.getValue().get(0).getDate(), quantity.getLeft(), quantity.getRight());
                result.add(curSum);
                continue;
            }
            var curSum = ProductionSum.newSum(null, new SumKey(curEntry.getKey()), SumKeyType.PART,
                    quantity.getLeft(), quantity.getRight(), curEntry.getValue().get(0).getDate());
            result.add(curSum);
        }
        return result;
    }

    @Override
    public Pair<ProduceQuantity, ProduceQuantity> summaryQuantity(List<MachineDailyProduction> productionDetail) {
        int quantity = 0, defectQuantity = 0;
        for (var production : productionDetail) {
            if (!production.getInspectQuantity().isZero()) {
                quantity += production.getInspectQuantity().getValue();
                defectQuantity += production.getInspectDefectQuantity().getValue();
            } else {
                quantity += production.getQuantity().getValue();
                defectQuantity += production.getDefectQuantity().getValue();
            }
        }
        return Pair.of(ProduceQuantity.of(quantity), ProduceQuantity.of(defectQuantity));
    }

    private String buildKey(ProduceOrderCode orderCode, SkuCode skuCode, Part part) {
        return orderCode.value() + "|" + skuCode.value() + "|" + part.value();
    }
}
