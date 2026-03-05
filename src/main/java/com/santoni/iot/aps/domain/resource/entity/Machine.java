package com.santoni.iot.aps.domain.resource.entity;

import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.organization.Institute;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Machine {

    private MachineId machineId;

    @Getter
    private OuterMachineId outerId;

    private MachineDeviceId deviceId;

    private MachineCode machineCode;

    @Getter
    private Institute institute;

    @Getter
    private MachineHierarchy hierarchy;

    @Getter
    private MachineSize machineSize;

    private MachineType machineType;

    private BareSpandex bareSpandex;

    private HighSpeed highSpeed;

    @Getter
    private List<MachineFeature> features;

    @Getter
    private MachineStatus status;

    public void change(MachineHierarchy newHierarchy,
                       MachineType newType,
                       BareSpandex newBareSpandex,
                       HighSpeed newHighSpeed,
                       List<MachineFeature> features) {
        if (!hierarchy.equals(newHierarchy)) {
            this.hierarchy = newHierarchy;
        }
        if (!machineType.equals(newType)) {
            this.machineType = newType;
        }
        if (!bareSpandex.equals(newBareSpandex)) {
            this.bareSpandex = newBareSpandex;
        }
        if (highSpeed.value() != newHighSpeed.value()) {
            this.highSpeed = newHighSpeed;
        }
        this.features = features;
    }

    public Machine(MachineId machineId,
                   OuterMachineId outerId,
                   MachineDeviceId deviceId,
                   MachineCode machineCode,
                   Institute institute,
                   MachineHierarchy hierarchy,
                   MachineSize machineSize,
                   MachineType machineType,
                   BareSpandex bareSpandex,
                   HighSpeed highSpeed,
                   List<MachineFeature> features,
                   MachineStatus status) {
        if (null == deviceId) {
            throw new IllegalArgumentException("deviceId不可为空");
        }
        if (null == machineCode) {
            throw new IllegalArgumentException("机器编号不可为空");
        }
        if (null == machineSize) {
            throw new IllegalArgumentException("机器需明确尺寸");
        }
        this.machineId = machineId;
        this.outerId = outerId;
        this.deviceId = deviceId;
        this.machineCode = machineCode;
        this.institute = institute;
        this.hierarchy = hierarchy;
        this.machineSize = machineSize;
        this.machineType = machineType;
        this.bareSpandex = bareSpandex;
        this.highSpeed = null == highSpeed ? HighSpeed.low() : highSpeed;
        this.features = features;
        this.status = status;
    }

    public boolean isHighSpeed() {
        return highSpeed.value();
    }

    public MachineId getId() {
        return machineId;
    }

    public MachineDeviceId getMachineDeviceId() {
        return deviceId;
    }

    public String getDeviceId() {
        return deviceId.value();
    }

    public String getCode() {
        return machineCode.value();
    }

    public String getType() {
        return null == machineType ? null : machineType.value();
    }

    public boolean isBareSpandexMachine() {
        return null != bareSpandex && StringUtils.isNotBlank(bareSpandex.value());
    }

    public String getBareSpandexType() {
        return null == bareSpandex ? null : bareSpandex.value();
    }
}
