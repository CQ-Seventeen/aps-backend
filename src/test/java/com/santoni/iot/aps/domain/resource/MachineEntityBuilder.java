package com.santoni.iot.aps.domain.resource;

import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineGroup;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleNumber;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;

import java.util.List;

public class MachineEntityBuilder {

    public static Machine getMachine() {
        return new Machine(new MachineId(1),
                null,
                new MachineDeviceId("device1"),
                new MachineCode("machine"),
                Institute.of(new InstituteId(1)),
                new MachineHierarchy(
                        Factory.of(new FactoryId(1)),
                        Workshop.of(new WorkshopId(1)),
                        MachineGroup.of(new MachineGroupId(1))),
                MachineSize.machineSize(new CylinderDiameter(14), new NeedleSpacing(28), new NeedleNumber(1248)),
                new MachineType("top2fast"),
                new BareSpandex("NONE"),
                HighSpeed.high(),
                List.of(),
                MachineStatus.IDLE
        );

    }
}
