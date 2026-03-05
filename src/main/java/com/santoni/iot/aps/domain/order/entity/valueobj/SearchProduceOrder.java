package com.santoni.iot.aps.domain.order.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.PageParam;
import com.santoni.iot.aps.domain.support.entity.valueobj.Search;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class SearchProduceOrder extends PageParam {

    private final FactoryId factoryId;

    private final ProduceOrderCode code;

    private final CustomerCode customerCode;

    private final List<ProduceOrderStatus> status;

    private final DeliveryTime start;

    private final DeliveryTime end;

    private final Search search;

    public SearchProduceOrder(FactoryId factoryId,
                              ProduceOrderCode code,
                              CustomerCode customerCode,
                              List<ProduceOrderStatus> status,
                              DeliveryTime start,
                              DeliveryTime end,
                              Search search,
                              CommonPageQuery query) {
        this.factoryId = factoryId;
        this.code = code;
        this.customerCode = customerCode;
        this.status = status;
        this.start = start;
        this.end = end;
        this.search = search;
        fromPageQuery(query);
    }

    public void nonPage() {
        this.setPageSize(-1);
        this.setPage(1);
    }
}
