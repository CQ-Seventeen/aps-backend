package com.santoni.iot.aps.application.plan.dto.factory;

import lombok.Data;

import java.util.List;

@Data
public class ProduceOrderDemandDTO {

    private String orderCode;

    private List<OrderDemandOfMachineDTO> demandOfMachine;
}
