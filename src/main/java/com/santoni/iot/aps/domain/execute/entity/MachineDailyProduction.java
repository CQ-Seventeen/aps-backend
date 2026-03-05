package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.AlteredQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ReportBarCode;
import com.santoni.iot.aps.domain.execute.util.SumKeyUtil;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineDeviceId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.RecordId;
import lombok.Getter;

@Getter
public class MachineDailyProduction {

    private RecordId recordId;

    private FactoryId factoryId;

    private TaskId taskId;

    private ProduceOrderCode orderCode;

    private SkuCode skuCode;

    private Part part;

    private ProduceDate date;

    private MachineDeviceId machineDeviceId;

    private ProduceQuantity quantity;

    private ProduceQuantity defectQuantity;

    private ProduceQuantity inspectQuantity;

    private ProduceQuantity inspectDefectQuantity;

    private ReportBarCode barCode;

    public static MachineDailyProduction fromPlannedTask(ProduceDate date,
                                                         PlannedTask plannedTask,
                                                         Machine machine,
                                                         ReportBarCode barCode) {
        return new MachineDailyProduction(
                null,
                plannedTask.getId(),
                machine.getHierarchy().getFactory().getId(),
                plannedTask.getProduceOrderCode(),
                plannedTask.getSkuCode(),
                plannedTask.getPart(),
                date,
                machine.getMachineDeviceId(),
                ProduceQuantity.zero(),
                ProduceQuantity.zero(),
                ProduceQuantity.zero(),
                ProduceQuantity.zero(),
                barCode
        );
    }

    public MachineDailyProduction(RecordId recordId,
                                  TaskId taskId,
                                  FactoryId factoryId,
                                  ProduceOrderCode orderCode,
                                  SkuCode skuCode,
                                  Part part,
                                  ProduceDate date,
                                  MachineDeviceId machineDeviceId,
                                  ProduceQuantity quantity,
                                  ProduceQuantity defectQuantity,
                                  ProduceQuantity inspectQuantity,
                                  ProduceQuantity inspectDefectQuantity,
                                  ReportBarCode barCode) {
        this.recordId = recordId;
        this.factoryId = factoryId;
        this.taskId = taskId;
        this.orderCode = orderCode;
        this.skuCode = skuCode;
        this.part = part;
        this.date = date;
        this.machineDeviceId = machineDeviceId;
        this.quantity = quantity;
        this.defectQuantity = defectQuantity;
        this.inspectQuantity = inspectQuantity;
        this.inspectDefectQuantity = inspectDefectQuantity;
        this.barCode = barCode;
    }

    public String getUniqueKey() {
        return orderCode.value() + "-" + skuCode.value() + "-" + part.value() + "-" + date.value() + "-" + machineDeviceId.value();
    }

    public AlteredQuantity changeQuantity(ProduceQuantity newQuantity) {
        var alter = AlteredQuantity.from(this.quantity, newQuantity);
        this.quantity = newQuantity;
        return alter;
    }

    public void changeQuantity(ProduceQuantity newQuantity, ProduceQuantity newDefectQuantity) {
        this.quantity = newQuantity;
        this.defectQuantity = newDefectQuantity;
    }

    public void changeInspectQuantity(ProduceQuantity newQuantity, ProduceQuantity newDefectQuantity) {
        this.inspectQuantity = newQuantity;
        this.inspectDefectQuantity = newDefectQuantity;
    }

    public String getSumKey() {
        return SumKeyUtil.buildComponentLevelKey(orderCode, skuCode, part);
    }
}