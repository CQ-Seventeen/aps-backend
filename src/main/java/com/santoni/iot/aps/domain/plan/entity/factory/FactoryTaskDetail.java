package com.santoni.iot.aps.domain.plan.entity.factory;

import com.santoni.iot.aps.domain.plan.entity.valueobj.OccupiedDays;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineNumber;
import lombok.Getter;

@Getter
public class FactoryTaskDetail {

    private TaskId taskId;

    private MachineNumber machineNumber;

    private OccupiedDays occupiedDays;

    private PlanPeriod planPeriod;

    private CylinderDiameter cylinderDiameter;

    public FactoryTaskDetail(TaskId taskId,
                             MachineNumber machineNumber,
                             OccupiedDays occupiedDays,
                             PlanPeriod planPeriod,
                             CylinderDiameter cylinderDiameter) {
        this.taskId = taskId;
        this.machineNumber = machineNumber;
        this.occupiedDays = occupiedDays;
        this.planPeriod = planPeriod;
        this.cylinderDiameter = cylinderDiameter;
    }

    public static FactoryTaskDetail newOf(MachineNumber machineNumber,
                                          OccupiedDays occupiedDays,
                                          PlanPeriod planPeriod,
                                          CylinderDiameter cylinderDiameter) {
        return new FactoryTaskDetail(null, machineNumber, occupiedDays, planPeriod, cylinderDiameter);
    }
}
