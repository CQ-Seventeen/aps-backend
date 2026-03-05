package com.santoni.iot.aps.application.execute.assembler;

import com.santoni.iot.aps.application.resource.dto.EstimateTimeDTO;
import com.santoni.iot.aps.application.execute.dto.ProductionAggregateByMachineDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.execute.entity.MachineAggregate;
import com.santoni.iot.aps.domain.execute.entity.ProduceEstimation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecuteDTOAssembler {

    public EstimateTimeDTO assembleEstimateTimeDTO(ProduceEstimation produceEstimation) {
        var dto = new EstimateTimeDTO();
        dto.setStartTime(TimeUtil.formatGeneralString(produceEstimation.getStartTime().value()));
        dto.setEndTime(TimeUtil.formatGeneralString(produceEstimation.getEndTime().value()));
        dto.setQuantity(produceEstimation.getQuantity().getValue());
        return dto;
    }

    public ProductionAggregateByMachineDTO assembleProductionAggregateByMachineDTO(MachineAggregate aggregate) {
        var dto = new ProductionAggregateByMachineDTO();
        dto.setCylinderDiameter(aggregate.getDiameter().value());
        dto.setNeedleSpacing(aggregate.getNeedleSpacing().value());
        dto.setTotalNum(aggregate.getTotalNum().value());
        dto.setRunningNum(aggregate.getRunningNum().value());
        dto.setTotalQuantity(aggregate.getTotalQuantity().getValue());
        dto.setProduceQuantity(aggregate.getProduceQuantity().getValue());
        dto.setLeftQuantity(aggregate.getLeftQuantity().getValue());
        dto.setLeftDaysBySingle(aggregate.getLeftDaysBySingle().getValue());
        return dto;
    }
}
