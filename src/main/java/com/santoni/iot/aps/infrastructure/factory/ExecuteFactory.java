package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;
import com.santoni.iot.aps.domain.execute.entity.ProductionSum;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ReportBarCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineDeviceId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.RecordId;
import com.santoni.iot.aps.infrastructure.po.ProductionSummaryPO;
import com.santoni.iot.aps.infrastructure.po.RecordMachineProductionPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ExecuteFactory {

    public MachineDailyProduction composeMachineDailyProduction(RecordMachineProductionPO po) {
        return new MachineDailyProduction(
                new RecordId(po.getId()),
                null == po.getTaskId() ? null : new TaskId(po.getTaskId()),
                null == po.getFactoryId() || po.getFactoryId() <= 0 ? null : new FactoryId(po.getFactoryId()),
                new ProduceOrderCode(po.getProduceOrderCode()),
                new SkuCode(po.getSkuCode()),
                new Part(po.getPart()),
                new ProduceDate(po.getDate()),
                new MachineDeviceId(po.getDeviceId()),
                ProduceQuantity.of(po.getQuantity()),
                ProduceQuantity.of(po.getDefectQuantity()),
                ProduceQuantity.of(po.getInspectQuantity()),
                ProduceQuantity.of(po.getInspectDefectQuantity()),
                StringUtils.isBlank(po.getBarCode()) ? null : new ReportBarCode(po.getBarCode())
        );
    }

    public RecordMachineProductionPO convertToRecordMachineProductionPO(MachineDailyProduction production) {
        var po = new RecordMachineProductionPO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != production.getRecordId()) {
            po.setId(production.getRecordId().value());
        }
        if (null != production.getTaskId()) {
            po.setTaskId(production.getTaskId().value());
        }
        po.setFactoryId(production.getFactoryId().value());
        po.setProduceOrderCode(production.getOrderCode().value());
        po.setSkuCode(production.getSkuCode().value());
        po.setPart(production.getPart().value());
        po.setDate(production.getDate().value());
        po.setDeviceId(production.getMachineDeviceId().value());
        po.setQuantity(production.getQuantity().getValue());
        po.setDefectQuantity(production.getDefectQuantity().getValue());
        po.setInspectQuantity(production.getInspectQuantity().getValue());
        po.setInspectDefectQuantity(production.getInspectDefectQuantity().getValue());
        if (null != production.getBarCode()) {
            po.setBarCode(production.getBarCode().value());
        }
        return po;
    }

    public ProductionSum composeProductionSum(ProductionSummaryPO po) {
        return new ProductionSum(
                new RecordId(po.getId()),
                new FactoryId(po.getFactoryId()),
                new SumKey(po.getSumKey()),
                SumKeyType.getByCode(po.getType()),
                ProduceQuantity.of(po.getQuantity()),
                ProduceQuantity.of(po.getTdQuantity()),
                ProduceQuantity.of(po.getDefectQuantity()),
                ProduceQuantity.of(po.getTdDefectQuantity()),
                new ProduceDate(po.getDate())
        );
    }

    public ProductionSummaryPO convertToProductionSummaryPO(ProductionSum production) {
        var po = new ProductionSummaryPO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != production.getFactoryId()) {
            po.setFactoryId(production.getFactoryId().value());
        }
        po.setSumKey(production.getKey().value());
        po.setType(production.getKeyType().getCode());
        po.setDate(production.getDate().value());
        po.setQuantity(production.getQuantity().getValue());
        po.setTdQuantity(production.getTillTodayQuantity().getValue());
        po.setDefectQuantity(production.getDefectQuantity().getValue());
        po.setTdDefectQuantity(production.getDefectTdQuantity().getValue());
        return po;
    }

}
