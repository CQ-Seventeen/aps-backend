package com.santoni.iot.aps.application.execute.impl;

import com.santoni.iot.aps.application.execute.ExecuteQueryApplication;
import com.santoni.iot.aps.application.execute.assembler.ExecuteDTOAssembler;
import com.santoni.iot.aps.application.execute.query.EstimateMachineWeaveTimeQuery;
import com.santoni.iot.aps.application.execute.query.EstimateMaxWaveQuantityQuery;
import com.santoni.iot.aps.application.resource.dto.EstimateTimeDTO;
import com.santoni.iot.aps.domain.execute.entity.ProduceEstimation;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ExecuteQueryApplicationImpl implements ExecuteQueryApplication {

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ExecuteDTOAssembler executeDTOAssembler;

    @Override
    public EstimateTimeDTO estimateWeaveTime(EstimateMachineWeaveTimeQuery query) {
        var estimation = prepareEstimation(query.getWeavingPartOrderId(), query.getMachineId(), query.getCanStartTime(), query.getCanEndTime());
        if (null == query.getStartTime()) {
            estimation.estimateTimeByEnd(new EndTime(query.getEndTime()), Quantity.of(query.getQuantity()));
        } else {
            estimation.estimateTimeByStart(new StartTime(query.getStartTime()), Quantity.of(query.getQuantity()));
        }
        return executeDTOAssembler.assembleEstimateTimeDTO(estimation);
    }

    @Override
    public int estimateMaxWeaveQuantity(EstimateMaxWaveQuantityQuery query) {
        var estimation = prepareEstimation(query.getWeavingPartOrderId(), query.getMachineId(), query.getStartTime(), query.getEndTime());
        estimation.estimateMaxQuantity(Quantity.of(query.getNeedQuantity()));
        return estimation.getQuantity().getValue();
    }

    private ProduceEstimation prepareEstimation(long weavingPartOrderId,
                                                long machineId,
                                                LocalDateTime startTime,
                                                LocalDateTime endTime) {
        var order = weavingOrderRepository.getPartOrderById(new WeavingPartOrderId(weavingPartOrderId));
        if (null == order) {
            throw new IllegalArgumentException("订单不存在");
        }
        var component = styleRepository.getComponentBySkuAndPart(order.getProduceOrderCode(), order.getDemand().getSkuCode(), order.getDemand().getPart());
        if (null == component) {
            throw new IllegalArgumentException("款式不存在");
        }
        var machine = machineRepository.detailById(new MachineId(machineId));
        if (null == machine) {
            throw new IllegalArgumentException("机器不存在:" + machineId);
        }
        return ProduceEstimation.basicOf(component, machine, PlanPeriod.of(startTime, endTime));
    }
}
