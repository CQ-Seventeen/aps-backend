package com.santoni.iot.aps.application.plan.dto.machine;

import com.santoni.iot.aps.common.utils.TimeUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MachineLevelDetailPlanDTO {

    private String startTime;

    private String endTime;

    private List<AggregateMachinePlanDTO> aggregateMachinePlanList;

    public static MachineLevelDetailPlanDTO empty(LocalDateTime startTime, LocalDateTime endTime) {
        var dto = new MachineLevelDetailPlanDTO();
        dto.setStartTime(TimeUtil.formatGeneralString(startTime));
        dto.setEndTime(TimeUtil.formatGeneralString(endTime));
        dto.setAggregateMachinePlanList(List.of());
        return dto;
    }
}
