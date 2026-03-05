package com.santoni.iot.aps.domain.support.entity.organization;

import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteName;
import lombok.Getter;

public class Institute {

    private final InstituteId id;

    private final InstituteName name;

    private Institute(InstituteId id, InstituteName name) {
        this.id = id;
        this.name = name;
    }

    public static Institute of(InstituteId id) {
        return of(id, null);
    }

    public static Institute of(InstituteId id, InstituteName name) {
        if (null == id) {
            throw new NullPointerException("企业id不可为空");
        }
        return new Institute(id, name);
    }

    public long getId() {
        return id.value();
    }

    public String getName() {
        return null == name ? null : name.value();
    }
}
