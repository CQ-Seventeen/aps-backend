package com.santoni.iot.aps.infrastructure.acl.feign.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchQueryStockReq {

    private List<YarnStockQueryRequest> yarnList;
}
