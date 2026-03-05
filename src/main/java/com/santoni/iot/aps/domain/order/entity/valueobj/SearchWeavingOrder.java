package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.constant.WeavingOrderStatus;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchWeavingOrder extends PageParam {

    private final FactoryId factoryId;

    private final WeavingOrderCode code;

    private final StyleCode styleCode;

    private final List<WeavingOrderStatus> status;

    public SearchWeavingOrder(FactoryId factoryId,
                              WeavingOrderCode code,
                              StyleCode styleCode,
                              List<WeavingOrderStatus> status, CommonPageQuery query) {
        this.factoryId = factoryId;
        this.code = code;
        this.styleCode = styleCode;
        this.status = status;
        fromPageQuery(query);
    }
}
