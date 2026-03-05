package com.santoni.iot.aps.domain.execute.service;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;
import com.santoni.iot.aps.domain.execute.entity.*;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ToOperateRecords;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ExecuteDomainService {

    ToOperateRecords handleRecordsUpdate(List<MachineDailyProduction> newRecords,
                                         List<MachineDailyProduction> existRecords);

    List<StyleComponentPredict> predictDailyProduction(List<MachineDailyProduction> prevProduction,
                                                       List<PlannedTask> curTasks,
                                                       Map<String, Map<String, StyleComponent>> componentMap,
                                                       LocalDateTime startTime,
                                                       LocalDateTime endTime);

    List<YarnUsage> gatherYarnUsage(List<StyleComponentPredict> predicts);

    void collectProductionByOrder(MachineAggregateTable aggregateTable,
                                  List<WeavingPartOrder> partOrders,
                                  List<ProductionSum> productionSum,
                                  List<StyleSku> skuList);

    List<ProductionSum> summaryDayProduction(List<MachineDailyProduction> curProduction,
                                             List<ProductionSum> prevDaySum,
                                             List<ProductionSum> latestSum,
                                             List<ProductionSum> curExistSUm);

    Pair<ProduceQuantity, ProduceQuantity> summaryQuantity(List<MachineDailyProduction> productionDetail);
}
