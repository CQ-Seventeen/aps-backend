package com.santoni.iot.aps.infrastructure.po.qo;

import lombok.Data;

import java.util.List;

@Data
public class SearchWeavingOrderQO {

    private long instituteId;

    private Long factoryId;

    private String code;

    private String styleCode;

    private List<Integer> statusList;
}
