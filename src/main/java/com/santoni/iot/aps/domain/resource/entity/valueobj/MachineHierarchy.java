package com.santoni.iot.aps.domain.resource.entity.valueobj;

import com.santoni.iot.aps.domain.support.entity.organization.Factory;
import com.santoni.iot.aps.domain.support.entity.organization.Workshop;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MachineHierarchy {

    private Factory factory;

    private Workshop workshop;

    private MachineGroup machineGroup;

    public MachineHierarchy(Factory factory, Workshop workshop, MachineGroup machineGroup) {
        if (null == factory) {
            throw new IllegalArgumentException("机器需指定工厂");
        }
        if (null == workshop) {
            throw new IllegalArgumentException("机器需指定车间");
        }
        if (null == machineGroup) {
            throw new IllegalArgumentException("机器需指定机组");
        }
        this.factory = factory;
        this.workshop = workshop;
        this.machineGroup = machineGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineHierarchy that)) return false;
        return factory.getId().value() == that.factory.getId().value() && workshop.getId().value() == that.workshop.getId().value()
                && machineGroup.getId().value() == that.machineGroup.getId().value();
    }

    @Override
    public int hashCode() {
        return Objects.hash(factory, workshop, machineGroup);
    }
}
