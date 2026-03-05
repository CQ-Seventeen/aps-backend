package com.santoni.iot.aps.infrastructure.repository;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ReportBarCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.execute.repository.ManuallyReportExecuteRepository;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.infrastructure.database.aps.ProductionSummaryMapper;
import com.santoni.iot.aps.infrastructure.database.aps.RecordMachineProductionMapper;
import com.santoni.iot.aps.infrastructure.factory.ExecuteFactory;
import com.santoni.iot.aps.infrastructure.po.ProductionSummaryPO;
import com.santoni.iot.aps.infrastructure.po.RecordMachineProductionPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManuallyReportExecuteRepositoryImpl implements ManuallyReportExecuteRepository {

    @Autowired
    private ProductionSummaryMapper productionSummaryMapper;

    @Autowired
    private RecordMachineProductionMapper recordMachineProductionMapper;

    @Autowired
    private ExecuteFactory executeFactory;

    @Override
    public List<MachineDailyProduction> listProductionByDate(ProduceDate date, FactoryId factoryId) {
        List<RecordMachineProductionPO> poList;
        if (null == factoryId) {
            poList = recordMachineProductionMapper.listByDate(PlanContext.getInstituteId(), date.value());
        } else {
            poList = recordMachineProductionMapper.listByFactoryAndDate(PlanContext.getInstituteId(), factoryId.value(), date.value());
        }
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeMachineDailyProduction(it)).toList();
    }

    @Override
    public ProductionSum findProductionSum(FactoryId factoryId, SumKey sumKey, SumKeyType type, ProduceDate date) {
        var po = productionSummaryMapper.findByFactoryAndKeyAndDate(PlanContext.getInstituteId(),
                factoryId.value(), date.value(),
                sumKey.value(), type.getCode());
        if (null != po) {
            return executeFactory.composeProductionSum(po);
        }
        po = productionSummaryMapper.findLatestByFactoryAndKey(PlanContext.getInstituteId(),
                factoryId.value(), sumKey.value(), type.getCode());
        if (null != po) {
            return executeFactory.composeProductionSum(po);
        }
        return null;
    }

    @Override
    public void saveProductionSum(ProductionSum productionSum) {
        var po = executeFactory.convertToProductionSummaryPO(productionSum);
        if (null == productionSum.getId()) {
            productionSummaryMapper.insert(po);
        } else {
            po.setId(productionSum.getId().value());
            productionSummaryMapper.update(po);
        }
    }

    @Override
    public List<ProductionSum> listProductionSumByDate(ProduceDate date, FactoryId factoryId, SumKeyType type) {
        var poList = null == factoryId ? productionSummaryMapper.listByKeyTypeAndDate(PlanContext.getInstituteId(), date.value(), type.getCode()) :
                productionSummaryMapper.listByFactoryAndKeyTypeAndDate(PlanContext.getInstituteId(), factoryId.value(), date.value(), type.getCode());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeProductionSum(it)).toList();
    }

    @Override
    public List<ProductionSum> listFactoryLatestProductionSumByKey(FactoryId factoryId, List<SumKey> keyList, SumKeyType type) {
        var poList = productionSummaryMapper.listLatestByFactoryAndKeyList(PlanContext.getInstituteId(),
                factoryId.value(), keyList.stream().map(SumKey::value).toList(), type.getCode());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeProductionSum(it)).toList();
    }

    @Override
    public List<ProductionSum> listLatestProductionSumByKey(List<SumKey> keyList, SumKeyType type) {
        var poList = productionSummaryMapper.listLatestByKeyList(PlanContext.getInstituteId(),
                keyList.stream().map(SumKey::value).toList(), type.getCode());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeProductionSum(it)).toList();
    }

    @Override
    public List<MachineDailyProduction> listFirstProductionOnSku(List<ProduceOrderCode> orderCodeList) {
        var poList = recordMachineProductionMapper.listOldestSkuRecordByOrder(PlanContext.getInstituteId(),
                orderCodeList.stream().map(ProduceOrderCode::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeMachineDailyProduction(it)).toList();
    }

    @Override
    public List<ProductionSum> listProductionSumByKeyAndDate(ProduceDate date, List<SumKey> keyList, boolean latest) {
        List<ProductionSummaryPO> poList;
        if (latest) {
            poList = productionSummaryMapper.listLatestByKeyListAndDate(PlanContext.getInstituteId(),
                    keyList.stream().map(SumKey::value).toList(),
                    date.value());
        } else {
            poList = productionSummaryMapper.listByKeyListAndDate(PlanContext.getInstituteId(),
                    keyList.stream().map(SumKey::value).toList(),
                    date.value());
        }
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeProductionSum(it)).toList();
    }

    @Override
    public List<MachineDailyProduction> listProductionByTaskId(TaskId taskId) {
        var poList = recordMachineProductionMapper.listByTaskId(PlanContext.getInstituteId(), taskId.value());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> executeFactory.composeMachineDailyProduction(it)).toList();
    }

    @Override
    public MachineDailyProduction findByBarCode(ReportBarCode barCode) {
        var po = recordMachineProductionMapper.findByBarCode(PlanContext.getInstituteId(), barCode.value());
        if (null == po) {
            return null;
        }
        return executeFactory.composeMachineDailyProduction(po);
    }

    @Override
    public void saveDailyProduction(MachineDailyProduction machineDailyProduction) {
        var po = executeFactory.convertToRecordMachineProductionPO(machineDailyProduction);
        if (null == po.getId()) {
            recordMachineProductionMapper.insert(po);
        } else {
            recordMachineProductionMapper.update(po);
        }
    }

}
