package com.santoni.iot.aps.infrastructure.acl.feign.request;

import lombok.Data;

import java.util.List;

@Data
public class WeavingScheduleRequest {

    private List<WeavingScheduleItem> scheduleList;

    private String fisMcClassSchecling;

    private String compId;

    private String deptId;

    private String signLabel;
}

