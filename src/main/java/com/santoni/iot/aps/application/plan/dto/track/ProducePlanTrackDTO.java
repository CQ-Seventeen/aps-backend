package com.santoni.iot.aps.application.plan.dto.track;

import lombok.Data;

import java.util.List;

@Data
public class ProducePlanTrackDTO {

    private long produceOrderId;

    private String produceOrderCode;

    private String deliveryDate;

    private int leftDays;

    private int totalQuantity;

    private int plannedQuantity;

    private List<WeavePartPlanTrackDTO> stylePlanTrackList;
}
