package com.santoni.iot.aps.domain.order.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.ProgramFile;
import com.santoni.iot.aps.domain.execute.util.SumKeyUtil;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlannedQuantity;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
public class WeavingPartOrder {

    private WeavingPartOrderId id;

    private FactoryId factoryId;

    private WeavingOrderId weavingOrderId;

    private ProduceOrderId produceOrderId;

    private ProduceOrderCode produceOrderCode;

    private StylePartDemand demand;

    private List<PlannedTask> plannedTasks;

    private Quantity plannedQuantity;

    private DeliveryTime deliveryTime;

    private ProgramFile program;

    private TaskDetailId taskDetailId;

    private Figure figure;

    private Unit unit;

    private OrderComment comment;

    private WeavingOrderStatus status;

    public void applyChange(WeavingPartOrder newOrder) {
        this.demand = newOrder.getDemand();
        this.deliveryTime = newOrder.getDeliveryTime();
        this.program = newOrder.getProgram();
        this.taskDetailId = newOrder.getTaskDetailId();
        this.figure = newOrder.getFigure();
        this.unit = newOrder.getUnit();
        this.comment = newOrder.getComment();
    }

    public int unPlannedQuantity() {
        return this.demand.getQuantity().getValue() - this.plannedQuantity.getValue();
    }

    public void changeDemand(StylePartDemand newDemand) {
        if (!this.demand.getSkuCode().equals(newDemand.getSkuCode()) || !this.demand.getPart().equals(newDemand.getPart())) {
            throw new UnsupportedOperationException("织造部件单款式不允许修改");
        }
        this.demand = newDemand;
    }

    public PlannedTask arrangeToMachine(MachinePlan machine, ProductionPlan plan) {
        if (!machine.canArrange(plan)) {
            throw new UnsupportedOperationException("计划时间段机器已被占用");
        }
        if (null == this.plannedQuantity) {
            this.plannedQuantity = Quantity.of(plan.getQuantity().getValue());
        } else {
            this.plannedQuantity = this.plannedQuantity.plus(plan.getQuantity().toQuantity());
        }
        this.status = this.status.toggleStatusAfterPlan(!this.plannedQuantity.lessThan(this.demand.getQuantity()));
        return PlannedTask.newTask(this.factoryId,
                this.produceOrderCode,
                this.id,
                machine.getMachine().getId(),
                this.demand.getStyleCode(),
                this.demand.getSkuCode(),
                this.demand.getPart(),
                this.demand.getSymbol(),
                this.demand.getSize(),
                this.demand.getColor(),
                plan);
    }

    public void returnPlannedQuantity(PlannedQuantity quantity) {
        this.plannedQuantity = this.getPlannedQuantity().minus(quantity.toQuantity());
    }

    public WeavingPartOrder(WeavingPartOrderId id,
                            FactoryId factoryId,
                            WeavingOrderId weavingOrderId,
                            ProduceOrderId produceOrderId,
                            ProduceOrderCode produceOrderCode,
                            StylePartDemand demand,
                            List<PlannedTask> plannedTasks,
                            Quantity plannedQuantity,
                            DeliveryTime deliveryTime,
                            ProgramFile program,
                            TaskDetailId taskDetailId,
                            Figure figure,
                            Unit unit,
                            OrderComment comment,
                            WeavingOrderStatus status) {
        this.id = id;
        this.factoryId = factoryId;
        this.weavingOrderId = weavingOrderId;
        this.produceOrderId = produceOrderId;
        this.produceOrderCode = produceOrderCode;
        this.demand = demand;
        this.plannedTasks = plannedTasks;
        this.plannedQuantity = plannedQuantity;
        this.deliveryTime = deliveryTime;
        this.program = program;
        this.taskDetailId = taskDetailId;
        this.figure = figure;
        this.unit = unit;
        this.comment = comment;
        this.status = status;
    }

    public boolean unPlanned() {
        return this.status == WeavingOrderStatus.CREATED
                || this.status == WeavingOrderStatus.PLANNING_PRODUCING
                || this.status == WeavingOrderStatus.PLANNING_NO_PRODUCE;
    }

    public String getSumKey() {
        return SumKeyUtil.buildComponentLevelKey(produceOrderCode, demand.getSkuCode(), demand.getPart());
    }
}
