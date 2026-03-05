package com.santoni.iot.aps.application.order.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageWeavingOrderQuery extends CommonPageQuery {

    private Long factoryId;

    private String code;

    private String styleCode;

    private List<Integer> status;

}
