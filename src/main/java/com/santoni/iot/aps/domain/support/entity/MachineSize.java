package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleNumber;
import lombok.Getter;

import java.util.Objects;

public class MachineSize {

    @Getter
    private CylinderDiameter cylinderDiameter;

    @Getter
    private NeedleSpacing needleSpacing;

    @Getter
    private NeedleNumber needleNumber;

    private MachineSize(CylinderDiameter cylinderDiameter, NeedleSpacing needleSpacing, NeedleNumber needleNumber) {
        this.cylinderDiameter = cylinderDiameter;
        this.needleSpacing = needleSpacing;
        this.needleNumber = needleNumber;
    }

    public static MachineSize machineSize(CylinderDiameter cylinderDiameter, NeedleSpacing needleSpacing, NeedleNumber needleNumber) {
        if (null == cylinderDiameter) {
            throw new IllegalArgumentException("筒径不可为空");
        }
        if (null == needleSpacing && null == needleNumber) {
            throw new IllegalArgumentException("针数、针距不可皆为空");
        }
        // todo 针距、针数计算待明确
        if (null == needleSpacing) {
            needleSpacing = new NeedleSpacing(cylinderDiameter.value() / needleNumber.value());
        }
        if (null == needleNumber) {
            needleNumber = new NeedleNumber(cylinderDiameter.value() / needleSpacing.value());
        }
        return new MachineSize(cylinderDiameter, needleSpacing, needleNumber);
    }

    public static MachineSize styleSize(CylinderDiameter cylinderDiameter, NeedleSpacing needleSpacing) {
        if (null == cylinderDiameter) {
            throw new IllegalArgumentException("筒径不可为空");
        }
        return new MachineSize(cylinderDiameter, needleSpacing, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineSize machineSize)) return false;
        return Objects.equals(cylinderDiameter, machineSize.cylinderDiameter) && Objects.equals(needleSpacing, machineSize.needleSpacing) && Objects.equals(needleNumber, machineSize.needleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cylinderDiameter, needleSpacing, needleNumber);
    }
}
