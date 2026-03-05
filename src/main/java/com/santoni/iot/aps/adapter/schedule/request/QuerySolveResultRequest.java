package com.santoni.iot.aps.adapter.schedule.request;

import lombok.Data;

import java.util.List;

@Data
public class QuerySolveResultRequest {

    private long taskId;

    private String startTime;

    private String endTime;

    private List<Integer> cylinderDiameterList;

}
