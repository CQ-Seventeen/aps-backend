package com.santoni.iot.aps.infrastructure.po.qo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchProduceOrderQO {

    private long instituteId;

    private String code;

    private String customerCode;

    private List<Integer> statusList;

    private String search;

    private LocalDateTime deliveryTimeStart;

    private LocalDateTime deliveryTimeEnd;
}
