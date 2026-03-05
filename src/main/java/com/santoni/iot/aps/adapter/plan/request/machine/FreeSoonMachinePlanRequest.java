package com.santoni.iot.aps.adapter.plan.request.machine;

import lombok.Data;

import java.util.List;

@Data
public class FreeSoonMachinePlanRequest {

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;

    private List<String> areaList;

    private String startTime;

    private String endTime;
}
