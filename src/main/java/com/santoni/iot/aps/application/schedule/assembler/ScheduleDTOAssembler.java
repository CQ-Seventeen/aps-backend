package com.santoni.iot.aps.application.schedule.assembler;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.application.plan.assembler.PlanDTOAssembler;
import com.santoni.iot.aps.application.resource.assembler.MachineDTOAssembler;
import com.santoni.iot.aps.application.schedule.context.AssembleScheduleLogContext;
import com.santoni.iot.aps.application.schedule.dto.*;
import com.santoni.iot.aps.application.support.assembler.TimePeriodAssembler;
import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.StyleWeavePlan;
import com.santoni.iot.aps.domain.schedule.entity.constant.ScheduleOperateType;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ScheduleDTOAssembler {

    @Autowired
    private TimePeriodAssembler timePeriodAssembler;

    @Autowired
    private PlanDTOAssembler planDTOAssembler;

    @Autowired
    private MachineDTOAssembler machineDTOAssembler;

    public ScheduleLogDTO assembleScheduleLogDTO(ScheduleLog log,
                                                 AssembleScheduleLogContext context) {
        var dto = new ScheduleLogDTO();
        dto.setLogId(log.getLogId().value());
        dto.setOperateType(log.getType().getCode());
        dto.setOperateTime(TimeUtil.formatGeneralString(log.getOperateTime().value()));
        if (ScheduleOperateType.STYLE_MACHINE == log.getType()) {
            dto.setDetail(assembleLogDetailFromPlannedTask(context.plannedTaskMap().get(log.getLogId().value()), context.orderIdMap(), context.machineMap()));
        } else {
            dto.setDetail(assembleLogDetailFromScheduleTask(context.scheduleTaskMap().get(log.getLogId().value()), context.orderIdMap(), context.machineMap()));
        }
        return dto;
    }

    private String assembleLogDetailFromPlannedTask(PlannedTask task,
                                                    Map<Long, WeavingPartOrder> orderIdMap,
                                                    Map<Long, Machine> machineMap) {
        var partOrder = orderIdMap.get(task.getWeavingPartOrderId().value());
        return "订单" + partOrder.getProduceOrderCode().value() + "的款式" + partOrder.getDemand().getSkuCode().value()
                + "下发" + task.getPlan().getQuantity().getValue() + "件至机台" + machineMap.get(task.getMachineId().value()).getDeviceId()
                + ",占据时间段:" + TimeUtil.formatGeneralString(task.getPlan().getPeriod().getStart().value()) + "至" + TimeUtil.formatGeneralString(task.getPlan().getPeriod().getEnd().value());
    }

    private String assembleLogDetailFromScheduleTask(ScheduleTask task,
                                                     Map<Long, WeavingPartOrder> orderIdMap,
                                                     Map<Long, Machine> machineMap) {
        Map<String, List<String>> produceOrderStyleMap = Maps.newHashMap();
        for (var demand : task.getStyleDemandList()) {
            var order = orderIdMap.get(demand.getWeavingPartOrderId().value());
            if (null == order) {
                continue;
            }
            var styleList = produceOrderStyleMap.get(order.getProduceOrderCode().value());
            if (null == styleList) {
                produceOrderStyleMap.put(order.getProduceOrderCode().value(), Lists.newArrayList(demand.getSkuCode().value()));
            } else {
                styleList.add(demand.getSkuCode().value());
            }
        }
        var deviceIdList = task.getMachineIds().stream().map(it -> machineMap.get(it.value())).filter(Objects::nonNull).map(Machine::getDeviceId).toList();
        var entryList = produceOrderStyleMap.entrySet().stream().toList();
        var styleDesc = entryList.size() > 3 ? entryList.subList(0, 3)
                .stream()
                .map(it -> assembleProduceOrderStyleDetail(it.getKey(), it.getValue()))
                .collect(Collectors.joining(";")) + "等" + entryList.size() + "个订单共" + task.getStyleDemandList().size() + "个款式" :
                entryList.stream()
                        .map(it -> assembleProduceOrderStyleDetail(it.getKey(), it.getValue()))
                        .collect(Collectors.joining(";"));
        return styleDesc + "在" + (deviceIdList.size() > 5 ? Joiner.on("、").join(deviceIdList.subList(0, 5)) + "等" + deviceIdList.size() + "台机器上自动排产" :
                Joiner.on("、").join(deviceIdList) + "共" + deviceIdList.size() + "台机器上自动排产");
    }

    private String assembleProduceOrderStyleDetail(String orderCode, List<String> styleCodeList) {
        return "订单" + orderCode + "的款式:" + (styleCodeList.size() > 3 ? Joiner.on("、").join(styleCodeList.subList(0, 3)) + "等" + styleCodeList.size() + "款"
                : Joiner.on("、").join(styleCodeList));
    }

    public ScheduleTaskDTO assembleScheduleTaskDTO(ScheduleTask task) {
        var dto = new ScheduleTaskDTO();
        dto.setTaskId(task.getTaskId().value());
        dto.setMachineIds(task.getMachineIds().stream().map(MachineId::value).toList());
        dto.setStyleDemands(task.getStyleDemandList().stream().map(this::assembleStyleWeaveDemandDTO).toList());
        dto.setTimePeriod(timePeriodAssembler.assembleTimePeriodDTO(task.getTimePeriod()));
        dto.setStatus(task.getStatus().getCode());
        if (null != task.getResult()) {
            dto.setSuggestion(task.getResult().getSuggestion());
            dto.setErrorInfo(task.getResult().getErrorInfo());
        }
        if (null != task.getHandleTime()) {
            dto.setSubmitTime(TimeUtil.formatGeneralString(task.getHandleTime().getSubmitTime()));
            dto.setBeginToProcessTime(TimeUtil.formatGeneralString(task.getHandleTime().getBeginToProcessTime()));
            dto.setFinishTime(TimeUtil.formatGeneralString(task.getHandleTime().getFinishTime()));
        }
        return dto;
    }

    private StyleWeaveDemandDTO assembleStyleWeaveDemandDTO(StylePartWeaveDemand demand) {
        var dto = new StyleWeaveDemandDTO();
        dto.setWeavingPartOrderId(demand.getWeavingPartOrderId().value());
        dto.setStyleCode(demand.getSkuCode().value());
        dto.setPart(demand.getPart().value());
        dto.setQuantity(demand.getQuantity().getValue());
        dto.setDeliveryTime(TimeUtil.formatGeneralString(demand.getTimePeriod().getEnd().value()));
        return dto;
    }

    public MachineScheduleResultDTO assembleMachineScheduleResultDTO(ScheduleOnMachine schedule,
                                                                     MachinePlan existPlan,
                                                                     AvailableTime availableTime) {
        var dto = new MachineScheduleResultDTO();
        dto.setMachine(machineDTOAssembler.assembleMachineDTO(existPlan.getMachine()));

        List<TimePeriodDTO> availablePeriods = Lists.newArrayList();
        List<TimePeriodDTO> unReachablePeriods = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        for (var period : availableTime.getAvailablePeriod()) {
            var split = period.splitTimePeriodByNow(now);
            if (null != split.getLeft()) {
                unReachablePeriods.add(timePeriodAssembler.assembleTimePeriodDTOFromUnReachable(split.getLeft()));
            }
            if (null != split.getRight()) {
                availablePeriods.add(timePeriodAssembler.assembleTimePeriodDTO(split.getRight()));
            }
        }
        dto.setAvailableTimePeriods(availablePeriods);
        dto.setUnReachableTimePeriods(unReachablePeriods);
        dto.setOccupiedTimePeriods(existPlan.expandPlannedTasks().stream().map(it -> planDTOAssembler.assembleTimePeriodDTO(it.getPlan().getPeriod())).toList());
        if (null != schedule) {
            dto.setScheduleTimePeriods(schedule.getStyleWeavePlanList().stream().map(this::assembleMachineSchedulePeriodDTO).toList());
        }
        return dto;
    }

    private MachineSchedulePeriodDTO assembleMachineSchedulePeriodDTO(StyleWeavePlan styleWeavePlan) {
        var dto = new MachineSchedulePeriodDTO();
        dto.setWeavingPartOrderId(styleWeavePlan.getWeavingPartOrderId().value());
        dto.setSkuCode(styleWeavePlan.getSkuCode().value());
        dto.setPart(styleWeavePlan.getPart().value());
        dto.setPlannedQuantity(styleWeavePlan.getQuantity().getValue());
        dto.setTimePeriod(timePeriodAssembler.assembleTimePeriodDTO(styleWeavePlan.getTimePeriod()));
        return dto;
    }
}
