package com.santoni.iot.aps.infrastructure.acl.feign.response;

import lombok.Data;

import java.util.List;

@Data
public class YarnStockResult {

    private List<YarnStock> yarnStockList;
}
