package com.santoni.iot.aps.application.order.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProduceOrderQuery extends CommonPageQuery {

    private Long factoryId;

    private String code;

    private String customerCode;

    private List<Integer> status;

    private LocalDateTime deliveryTimeStart;

    private LocalDateTime deliveryTimeEnd;

    private String search;
}
