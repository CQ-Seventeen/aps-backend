package com.santoni.iot.aps.domain.support.entity.organization;

import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryCode;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import lombok.Getter;

@Getter
public class Factory {

    private FactoryId id;

    private FactoryCode code;

    private Factory(FactoryId id, FactoryCode code) {
        this.id = id;
        this.code = code;
    }

    public static Factory of(FactoryId id) {
        return of(id, null);
    }

    public static Factory of(FactoryId id, FactoryCode code) {
        if (null == id) {
            throw new NullPointerException("工厂id不可为空");
        }
        return new Factory(id, code);
    }
}
