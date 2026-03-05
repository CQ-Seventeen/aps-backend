package com.santoni.iot.aps.domain.schedule.solver.or.fact;

import lombok.Data;

@Data
public class ToWeavedStyle {

    private long weavingOrderId;

    private String styleCode;

    private int timePerPiece;

    private int totalQuantity;
}
