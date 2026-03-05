package com.santoni.iot.aps.application.plan.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterMachineQuery {

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;

    private List<String> machineTypeList;

    private List<String> bareSpandexTypeList;

    private Boolean highSpeed;

//    private List<>

    public FilterMachineQuery(List<String> machineTypeList, List<String> bareSpandexTypeList, Boolean highSpeed) {
        this.cylinderDiameterList = List.of();
        this.needleSpacingList = List.of();
        this.machineTypeList = machineTypeList;
        this.bareSpandexTypeList = bareSpandexTypeList;
        this.highSpeed = highSpeed;
    }
}
