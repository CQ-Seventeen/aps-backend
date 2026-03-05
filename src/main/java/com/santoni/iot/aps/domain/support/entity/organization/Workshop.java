package com.santoni.iot.aps.domain.support.entity.organization;

import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopCode;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import lombok.Getter;

@Getter
public class Workshop {

    private WorkshopId id;

    private WorkshopCode code;

    private Workshop(WorkshopId id, WorkshopCode code) {
        this.id = id;
        this.code = code;
    }

    public static Workshop of(WorkshopId id) {
        return of(id, null);
    }

    public static Workshop of(WorkshopId id, WorkshopCode code) {
        if (null == id) {
            throw new NullPointerException("车间id不可为空");
        }
        return new Workshop(id, code);
    }
}
