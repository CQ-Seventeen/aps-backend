package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineNumber;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import com.santoni.iot.aps.domain.support.entity.valueobj.ProduceDays;
import lombok.Getter;

import java.util.List;

@Getter
public class MachineAggregate {

    private CylinderDiameter diameter;

    private NeedleSpacing needleSpacing;

    private MachineNumber totalNum;

    private MachineNumber runningNum;

    private Quantity totalQuantity;

    private ProduceQuantity produceQuantity;

    private ProduceQuantity leftQuantity;

    private ProduceDays leftDaysBySingle;

    public void collectPartOrder(WeavingPartOrder partOrder,
                                 ProductionSum production,
                                 StyleComponent styleComponent) {
        this.totalQuantity = totalQuantity.plus(partOrder.getDemand().getQuantity());
        var orderLeftQuantity = null == production ? partOrder.getDemand().getQuantity() : partOrder.getDemand().getQuantity().minusProduceQuantity(production.getTillTodayQuantity());
        if (null != production) {
            this.produceQuantity = produceQuantity.plus(production.getTillTodayQuantity());
        }
        this.leftQuantity = leftQuantity.plus(orderLeftQuantity);
        var days = styleComponent.theoreticalDays(orderLeftQuantity);
        this.leftDaysBySingle.add(days);
    }

    public static MachineAggregate initFrom(CylinderDiameter diameter, NeedleSpacing needleSpacing, List<Machine> machines) {
        var running = machines.stream().filter(it -> it.getStatus() == MachineStatus.RUNNING).toList();
        return new MachineAggregate(diameter, needleSpacing, new MachineNumber(machines.size()), new MachineNumber(running.size()),
                Quantity.zero(), ProduceQuantity.zero(), ProduceQuantity.zero(), ProduceDays.zero());
    }

    public void collectMachine(List<Machine> machines) {
        var running = machines.stream().filter(it -> it.getStatus() == MachineStatus.RUNNING).toList();
        this.totalNum = new MachineNumber(totalNum.value() + machines.size());
        this.runningNum = new MachineNumber(runningNum.value() + running.size());
    }

    public MachineAggregate(CylinderDiameter diameter,
                            NeedleSpacing needleSpacing,
                            MachineNumber totalNum,
                            MachineNumber runningNum,
                            Quantity totalQuantity,
                            ProduceQuantity produceQuantity,
                            ProduceQuantity leftQuantity,
                            ProduceDays leftDaysBySingle) {
        this.diameter = diameter;
        this.needleSpacing = needleSpacing;
        this.totalNum = totalNum;
        this.runningNum = runningNum;
        this.totalQuantity = totalQuantity;
        this.produceQuantity = produceQuantity;
        this.leftQuantity = leftQuantity;
        this.leftDaysBySingle = leftDaysBySingle;
    }
}
