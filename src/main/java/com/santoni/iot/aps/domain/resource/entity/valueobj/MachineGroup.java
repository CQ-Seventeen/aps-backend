package com.santoni.iot.aps.domain.resource.entity.valueobj;

import lombok.Getter;

@Getter
public class MachineGroup {

    private MachineGroupId id;

    private MachineGroupCode code;

    private MachineGroup(MachineGroupId id, MachineGroupCode code) {
        this.id = id;
        this.code = code;
    }

    public static MachineGroup of(MachineGroupId id) {
        return of(id, null);
    }

    public static MachineGroup of(MachineGroupId id, MachineGroupCode code) {
        if (null == id) {
            throw new NullPointerException("机组id不可为空");
        }
        return new MachineGroup(id, code);
    }
}
