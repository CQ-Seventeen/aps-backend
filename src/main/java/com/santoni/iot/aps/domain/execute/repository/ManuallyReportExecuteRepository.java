package com.santoni.iot.aps.domain.execute.repository;

import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ReportBarCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;

import java.util.List;

public interface ManuallyReportExecuteRepository {

    List<MachineDailyProduction> listProductionByDate(ProduceDate date, FactoryId factoryId);

    ProductionSum findProductionSum(FactoryId factoryId, SumKey sumKey, SumKeyType type, ProduceDate date);

    void saveProductionSum(ProductionSum productionSum);

    List<ProductionSum> listProductionSumByDate(ProduceDate date, FactoryId factoryId, SumKeyType type);

    List<ProductionSum> listFactoryLatestProductionSumByKey(FactoryId factoryId, List<SumKey> keyList, SumKeyType type);

    List<ProductionSum> listLatestProductionSumByKey(List<SumKey> keyList, SumKeyType type);

    List<MachineDailyProduction> listFirstProductionOnSku(List<ProduceOrderCode> orderCodeList);

    List<ProductionSum> listProductionSumByKeyAndDate(ProduceDate date, List<SumKey> keyList, boolean latest);

    List<MachineDailyProduction> listProductionByTaskId(TaskId taskId);

    MachineDailyProduction findByBarCode(ReportBarCode barCode);

    void saveDailyProduction(MachineDailyProduction machineDailyProduction);
}
