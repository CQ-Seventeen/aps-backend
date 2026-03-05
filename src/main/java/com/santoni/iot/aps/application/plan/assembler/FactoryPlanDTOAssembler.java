package com.santoni.iot.aps.application.plan.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.plan.context.BuildMachineCapacityContext;
import com.santoni.iot.aps.application.plan.dto.factory.ProduceOrderDemandDTO;
import com.santoni.iot.aps.application.plan.dto.factory.OrderDemandOfMachineDTO;
import com.santoni.iot.aps.application.plan.context.BuildFactoryPlanContext;
import com.santoni.iot.aps.application.plan.dto.factory.*;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTaskDetail;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FactoryPlanDTOAssembler {

    public List<MachineCapacityDTO> assembleMachineCapacityList(LocalDateTime start,
                                                                LocalDateTime end,
                                                                BuildMachineCapacityContext context) {
        List<MachineCapacityDTO> res = Lists.newArrayListWithExpectedSize(context.cylinderList().size());

        var days = (double) Duration.between(start, end).getSeconds() / (24 * 3600);
        for (var cylinderDiameter : context.cylinderList()) {
            List<FactoryMachineCapacityDTO> factoryCapacityList = Lists.newArrayListWithExpectedSize(context.factoryList().size());
            var cylinderMachineMap = context.machineMap().get(cylinderDiameter);
            for (var factory : context.factoryList()) {
                var dto = new FactoryMachineCapacityDTO();
                dto.setCylinderDiameter(cylinderDiameter);
                dto.setFactoryId(factory.getId().value());
                dto.setFactoryCode(factory.getCode().value());
                List<Machine> machineList = MapUtils.isEmpty(cylinderMachineMap) ? List.of() : cylinderMachineMap.get(factory.getId().value());
                if (CollectionUtils.isNotEmpty(machineList)) {
                    dto.setMachineNumber(machineList.size());
                    dto.setOpenedMachineNumber((int) machineList.stream().filter(it -> it.getStatus() != MachineStatus.SHUT_DOWN).count());
                }
                dto.setTotalMachineDays(days * dto.getOpenedMachineNumber());
                List<FactoryTaskDetail> taskDetailList = MapUtils.isEmpty(context.taskMap().get(factory.getId().value())) ? List.of() :
                        context.taskMap().get(factory.getId().value()).get(cylinderDiameter);
                if (CollectionUtils.isNotEmpty(taskDetailList)) {
                    dto.setOccupiedMachineDays(taskDetailList.stream().mapToDouble(it -> it.getOccupiedDays().value() * it.getMachineNumber().value()).sum());
                }
                double available = dto.getTotalMachineDays() - dto.getOccupiedMachineDays();
                dto.setAvailableMachineDays(Double.compare(available, 0) > 0 ? available : 0);
                factoryCapacityList.add(dto);
            }
            var dto = new MachineCapacityDTO();
            dto.setCylinderDiameter(cylinderDiameter);
            dto.setFactoryCapacityList(factoryCapacityList);
            // 这里工厂数不多 采用多次stream求和
            dto.setMachineNumber(factoryCapacityList.stream().mapToInt(FactoryMachineCapacityDTO::getMachineNumber).sum());
            dto.setOpenedMachineNumber(factoryCapacityList.stream().mapToInt(FactoryMachineCapacityDTO::getOpenedMachineNumber).sum());
            dto.setTotalMachineDays(factoryCapacityList.stream().mapToDouble(FactoryMachineCapacityDTO::getTotalMachineDays).sum());
            dto.setAvailableMachineDays(factoryCapacityList.stream().mapToDouble(FactoryMachineCapacityDTO::getAvailableMachineDays).sum());
            dto.setOccupiedMachineDays(factoryCapacityList.stream().mapToDouble(FactoryMachineCapacityDTO::getOccupiedMachineDays).sum());
            res.add(dto);
        }
        return res;
    }

    public FactoryOrderDimPlanDTO assembleFactoryOrderDimPlanDTO(LocalDateTime start,
                                                                 LocalDateTime end,
                                                                 ProduceOrderCode orderCode,
                                                                 BuildFactoryPlanContext context) {
        List<FactoryMachineDimPlanDTO> planList = Lists.newArrayListWithExpectedSize(context.cylinderList().size());
        var taskAndDetailMap = context.taskList().stream()
                .collect(Collectors.teeing(
                        Collectors.toMap(it -> it.getTaskId().value(),
                                it -> it.getProduceOrderCode().value(),
                                (v1, v2) -> v1),
                        Collectors.flatMapping(it -> it.getAssignDetail().stream(), Collectors.groupingBy(it -> it.getCylinderDiameter().value())),
                        Pair::of
                ));
        var days = (double) Duration.between(start, end).getSeconds() / (24 * 3600);
        for (var cylinder : context.cylinderList()) {
            var dto = new FactoryMachineDimPlanDTO();
            dto.setMachineNumber(null == context.machineMap().get(cylinder) ? 0 : context.machineMap().get(cylinder).size());
            dto.setTotalDays(days * dto.getMachineNumber());
            // todo
            dto.setOpenedNumber(dto.getMachineNumber());
            dto.setCylinderDiameter(cylinder);
            var detailList = taskAndDetailMap.getRight().get(cylinder);
            if (CollectionUtils.isEmpty(detailList)) {
                dto.setAvailableDays(dto.getTotalDays());
                planList.add(dto);
                continue;
            }
            double occupiedDays = 0;
            double orderOccupiedDays = 0;
            List<FactoryPlannedPeriodDTO> periods = Lists.newArrayListWithExpectedSize(detailList.size());
            for (var detail : detailList) {
                var periodDTO = new FactoryPlannedPeriodDTO();
                periodDTO.setOccupiedDays(detail.getOccupiedDays().value());
                periodDTO.setProduceOrderCode(taskAndDetailMap.getLeft().get(detail.getTaskId().value()));
                periodDTO.setStartTime(TimeUtil.formatGeneralString(detail.getPlanPeriod().getStart().value()));
                periodDTO.setEndTime(TimeUtil.formatGeneralString(detail.getPlanPeriod().getEnd().value()));
                occupiedDays += (detail.getOccupiedDays().value()) * dto.getMachineNumber();
                if (StringUtils.equals(periodDTO.getProduceOrderCode(), orderCode.value())) {
                    orderOccupiedDays += (detail.getOccupiedDays().value()) * dto.getMachineNumber();
                }
                periods.add(periodDTO);
            }
            dto.setPeriods(periods);
            dto.setOccupiedDays(occupiedDays);
            dto.setOrderOccupiedDays(orderOccupiedDays);
            dto.setAvailableDays(dto.getTotalDays() - occupiedDays);
            planList.add(dto);
        }
        var dto = new FactoryOrderDimPlanDTO();
        dto.setMachineDimPlanList(planList);
        dto.setOrderDemand(assembleOrderDemandDTO(orderCode, context));
        dto.setStartTime(TimeUtil.formatGeneralString(start));
        dto.setEndTime(TimeUtil.formatGeneralString(end));
        return dto;
    }

    private ProduceOrderDemandDTO assembleOrderDemandDTO(ProduceOrderCode orderCode, BuildFactoryPlanContext context) {
        List<OrderDemandOfMachineDTO> demandDTOList = Lists.newArrayListWithExpectedSize(context.componentMap().size());
        for (var cylinder : context.cylinderList()) {
            var componentList = context.componentMap().get(cylinder);
            demandDTOList.add(assembleOrderDemandOfMachineDTO(context.demandMap(), context.skuMap(), componentList, cylinder));
        }
        var dto = new ProduceOrderDemandDTO();
        dto.setDemandOfMachine(demandDTOList);
        dto.setOrderCode(orderCode.value());
        return dto;
    }

    private OrderDemandOfMachineDTO assembleOrderDemandOfMachineDTO(Map<String, StyleDemand> demandMap,
                                                                   Map<String, StyleSku> skuMap,
                                                                   List<StyleComponent> componentList,
                                                                   int cylinderDiameter) {
        var componentMap = componentList.stream().collect(Collectors.groupingBy(it -> it.getSkuCode().value()));
        double totalSeconds = 0;
        List<StylePartDemandDTO> demandDTOList = Lists.newArrayListWithExpectedSize(componentMap.size());
        for (var entry : componentMap.entrySet()) {
            var demand = demandMap.get(entry.getKey());
            if (null == demand) {
                continue;
            }
            var sku = skuMap.get(entry.getKey());
            if (null == sku) {
                continue;
            }
            var dto = new StylePartDemandDTO();
            dto.setStyleCode(sku.getStyleCode().value());
            dto.setSize(sku.getSize().value());
            dto.setQuantity(demand.getOrderQuantity().getValue());
            List<String> partList = Lists.newArrayListWithExpectedSize(entry.getValue().size());
            for (var part : entry.getValue()) {
                partList.add(part.getPart().value());
                totalSeconds += (part.getNumber().value() * demand.getOrderQuantity().getValue() / (double) part.getRatio().value()) * part.getExpectedProduceTime().value();
            }
            dto.setPart(partList);
            demandDTOList.add(dto);
        }
        var dto = new OrderDemandOfMachineDTO();
        dto.setDemands(demandDTOList);
        dto.setCylinderDiameter(cylinderDiameter);
        dto.setTotalDays(Math.ceil(10 * totalSeconds / PlanConstant.SECONDS_PER_DAY) / 10);
        return dto;
    }
}
