package com.santoni.iot.aps.domain.plan.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.Getter;

@Getter
public class SearchPlannedTask extends PageParam {

    private final FactoryId factoryId;

    private final ProduceOrderCode produceOrderCode;

    private final StyleCode styleCode;

    private final StartTime startTime;

    private final EndTime endTime;

    public SearchPlannedTask(FactoryId factoryId,
                             ProduceOrderCode produceOrderCode,
                             StyleCode styleCode,
                             StartTime startTime,
                             EndTime endTime,
                             CommonPageQuery query) {
        this.factoryId = factoryId;
        this.produceOrderCode = produceOrderCode;
        this.styleCode = styleCode;
        this.startTime = startTime;
        this.endTime = endTime;
        fromPageQuery(query);
    }
}
