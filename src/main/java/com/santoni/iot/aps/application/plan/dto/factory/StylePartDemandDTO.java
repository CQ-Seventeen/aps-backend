package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class StylePartDemandDTO {

    private String styleCode;

    private String size;

    private List<String> part;

    private int quantity;
}
