package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FreeSoonMachinePlanQuery {

    private Long factoryId;

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;

    private List<String> areaList;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
