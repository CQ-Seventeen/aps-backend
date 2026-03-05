package com.santoni.iot.aps.application.plan.query;

import lombok.Data;

import java.util.List;

@Data
public class CanScheduleMachinePlanQuery {

    private List<Long> weavingPartOrderIds;

}
