package com.santoni.iot.aps.application.order.dto;

import com.santoni.iot.aps.application.support.dto.FactoryDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProduceOrderDTO {

    private long orderId;

    private String orderCode;

    private String customerName;

    private String customerCode;

    private List<StyleDemandDTO> demands;

    private String deliveryTime;

    private String manufactureBatch;

    private String manufactureDate;

    private int status;

    private FactoryDTO assignedFactory;
}
