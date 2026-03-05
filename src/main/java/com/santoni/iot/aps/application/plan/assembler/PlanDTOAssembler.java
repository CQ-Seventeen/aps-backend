package com.santoni.iot.aps.application.plan.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.bom.assembler.StyleAssembler;
import com.santoni.iot.aps.application.plan.context.BuildMachineTaskDetailContext;
import com.santoni.iot.aps.application.plan.dto.machine.*;
import com.santoni.iot.aps.application.plan.dto.order.WeavingPlannedPeriodDTO;
import com.santoni.iot.aps.application.plan.dto.track.*;
import com.santoni.iot.aps.application.resource.assembler.MachineDTOAssembler;
import com.santoni.iot.aps.application.support.assembler.TimePeriodAssembler;
import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.execute.entity.OrderProduction;
import com.santoni.iot.aps.domain.execute.entity.StyleComponentPredict;
import com.santoni.iot.aps.domain.execute.entity.WeaveProduceTrack;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.DailyMachineUsage;
import com.santoni.iot.aps.domain.plan.entity.valueobj.MachineUsage;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.DatePeriod;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlanDTOAssembler {

    @Autowired
    private TimePeriodAssembler timePeriodAssembler;

    @Autowired
    private MachineDTOAssembler machineDTOAssembler;

    @Autowired
    private StyleAssembler styleAssembler;


    public MachineTaskDetailDTO assembleMachineTaskDetailDTO(PlannedTask task, BuildMachineTaskDetailContext context) {
        var dto = new MachineTaskDetailDTO();
        dto.setTaskId(task.getId().value());
        if (null != task.getTaskCode()) {
            dto.setTaskCode(task.getTaskCode().value());
        }
        if (null != context.machine()) {
            dto.setMachine(machineDTOAssembler.assembleMachineDTO(context.machine()));
        }
        dto.setProduceOrderCode(task.getProduceOrderCode().value());
        dto.setDeliveryDate(TimeUtil.formatYYYYMMDD(context.weavingPartOrder().getDeliveryTime().value()));
        dto.setWeavingPartOrderId(task.getWeavingPartOrderId().value());
        dto.setStyleCode(context.styleSpu().getCode().value());
        dto.setStyleName(context.styleSpu().getName().value());

        if (null != task.getSymbol()) {
            dto.setSymbolId(task.getSymbol().id());
            dto.setSymbol(task.getSymbol().value());
        }
        
        if (null != task.getSize()) {
            dto.setSizeId(task.getSize().id());
            dto.setSize(task.getSize().value());
        }
        if (null != task.getSkuCode()) {
            dto.setSkuCode(task.getSkuCode().value());
        }
        if (null != task.getColor()) {
            dto.setColorId(task.getColor().id());
            dto.setColor(task.getColor().value());
        }
        if (null != task.getPart()) {
            dto.setPartId(task.getPart().id());
            dto.setPart(task.getPart().value());
        }
        dto.setStyleComponent(styleAssembler.assembleStyleComponentDTO(context.styleComponent()));
        dto.setPlanStartTime(TimeUtil.formatGeneralString(task.getPlan().getPeriod().getStart().value()));
        dto.setPlanEndTime(TimeUtil.formatGeneralString(task.getPlan().getPeriod().getEnd().value()));
        dto.setPlannedQuantity(task.getPlan().getQuantity().getValue());
        dto.setProducedQuantity(task.getProduceQuantity().getValue());
        if (null != task.getExecutePeriod()) {
            dto.setExecuteStartTime(TimeUtil.formatGeneralString(task.getExecutePeriod().getStart().value()));
            dto.setExecuteEndTime(TimeUtil.formatGeneralString(task.getExecutePeriod().getEnd().value()));
        }
        return dto;
    }

    public MachineTaskListDTO assembleMachineTaskDTO(PlannedTask task, Map<Long, Machine> machineMap) {
        var dto = new MachineTaskListDTO();
        dto.setTaskId(task.getId().value());
        if (null != task.getTaskCode()) {
            dto.setTaskCode(task.getTaskCode().value());
        }
        dto.setMachineId(task.getMachineId().value());
        var machine = machineMap.get(task.getMachineId().value());
        if (null != machine) {
            dto.setDeviceId(machine.getDeviceId());
            dto.setCylinderDiameter(machine.getMachineSize().getCylinderDiameter().value());
        }
        dto.setProduceOrderCode(task.getProduceOrderCode().value());
        dto.setWeavingPartOrderId(task.getWeavingPartOrderId().value());
        if (null != task.getStyleCode()) {
            dto.setStyleCode(task.getStyleCode().value());
        }
        if (null != task.getSkuCode()) {
            dto.setSkuCode(task.getSkuCode().value());
        }
        if (null != task.getSize()) {
            dto.setSize(task.getSize().value());
        }
        if (null != task.getColor()) {
            dto.setColor(task.getColor().value());
        }
        dto.setPart(task.getPart().value());
        dto.setPlanStartTime(TimeUtil.formatGeneralString(task.getPlan().getPeriod().getStart().value()));
        dto.setPlanEndTime(TimeUtil.formatGeneralString(task.getPlan().getPeriod().getEnd().value()));
        dto.setPlannedQuantity(task.getPlan().getQuantity().getValue());
        dto.setProducedQuantity(task.getProduceQuantity().getValue());
        if (null != task.getExecutePeriod()) {
            dto.setExecuteStartTime(TimeUtil.formatGeneralString(task.getExecutePeriod().getStart().value()));
            dto.setExecuteEndTime(TimeUtil.formatGeneralString(task.getExecutePeriod().getEnd().value()));
        }
        dto.setStatus(task.getStatus().getCode());
        if (null != task.getTaskFlag()) {
            dto.setFlag(task.getTaskFlag().value());
        }
        return dto;
    }

    public List<MachineDailyUsageDTO> assembleMachineDailyUsageList(MachineUsage usage, List<DatePeriod> dateList) {
        List<MachineDailyUsageDTO> res = Lists.newArrayListWithExpectedSize(dateList.size());
        for (var date : dateList) {
            var dailyUsage = usage.getDailyUsage(date.getDate());
            res.add(assembleMachineDailyUsageDTO(dailyUsage, date.getDate()));
        }
        return res;
    }

    private MachineDailyUsageDTO assembleMachineDailyUsageDTO(DailyMachineUsage dailyUsage, String date) {
        var dto = new MachineDailyUsageDTO(date);
        if (null != dailyUsage) {
            dto.setTotalCount(dailyUsage.getCount());
            dto.setWorkingCount(dailyUsage.getInUseCount());
        }
        return dto;
    }

    public WeavingPlannedPeriodDTO assemblePlannedPeriodDTO(TaskSegment taskSegment, SkuCode skuCode) {
        var dto = new WeavingPlannedPeriodDTO();
        dto.setStyleCode(skuCode.value());
        dto.setSegmentId(taskSegment.getId().value());
        dto.setTimePeriod(assembleTimePeriodDTO(taskSegment.getPlan().getPeriod()));
        dto.setPlannedQuantity(taskSegment.getPlan().getQuantity().getValue());
        if (null != taskSegment.getExecution()) {
            dto.setProduceQuantity(taskSegment.getExecution().getQuantity().getValue());
        }
        dto.setStatus(taskSegment.getStatus().getCode());
        return dto;
    }

    public TimePeriodDTO assembleTimePeriodDTO(PlanPeriod planPeriod) {
        return new TimePeriodDTO(TimeUtil.formatGeneralString(planPeriod.getStart().value()),
                TimeUtil.formatGeneralString(planPeriod.getEnd().value()),
                planPeriod.totalSeconds());
    }

    public MachineLevelDetailPlanDTO assembleMachineLevelDetailPlanDTO(List<MachinePlan> machinePlanList, LocalDateTime startTime, LocalDateTime endTime) {
        var dto = new MachineLevelDetailPlanDTO();
        dto.setStartTime(TimeUtil.formatGeneralString(startTime));
        dto.setEndTime(TimeUtil.formatGeneralString(endTime));
        long totalSeconds = Duration.between(startTime, endTime).getSeconds();
        var now = LocalDateTime.now();
        if (CollectionUtils.isNotEmpty(machinePlanList)) {
            dto.setAggregateMachinePlanList(assembleAggregateMachinePlanList(machinePlanList, now, totalSeconds));
        }
        return dto;
    }

    public List<AggregateMachinePlanDTO> assembleAggregateMachinePlanList(List<MachinePlan> machinePlanList, LocalDateTime now, Long totalSeconds) {
        var machineMap = machinePlanList.stream().collect(Collectors.groupingBy(it -> it.getMachine().getMachineSize().getCylinderDiameter().value()));
        List<AggregateMachinePlanDTO> res = Lists.newArrayListWithExpectedSize(machineMap.size());
        for (var entry : machineMap.entrySet()) {
            var dto = new AggregateMachinePlanDTO();
            dto.setCylinderDiameter(entry.getKey());
            dto.setMachinePlanList(entry.getValue().stream().map(it -> assembleMachinePlanDetailDTO(it, now, totalSeconds)).toList());
            res.add(dto);
        }
        res.sort(Comparator.comparing(AggregateMachinePlanDTO::getCylinderDiameter));
        return res;
    }

    public Pair<List<TimePeriodDTO>, List<TimePeriodDTO>> assembleAvailableTimePeriodDTO(List<TimePeriod> periods, LocalDateTime now) {
        List<TimePeriodDTO> availableTimePeriods = Lists.newArrayList();
        List<TimePeriodDTO> unreachableTimePeriods = Lists.newArrayList();
        fillAvailableTimePeriods(availableTimePeriods, unreachableTimePeriods, periods, now);
        return Pair.of(availableTimePeriods, unreachableTimePeriods);
    }

    private MachinePlanDetailDTO assembleMachinePlanDetailDTO(MachinePlan machinePlan, LocalDateTime now, Long totalSeconds) {
        var dto = new MachinePlanDetailDTO();
        dto.setMachine(machineDTOAssembler.assembleMachineDTO(machinePlan.getMachine()));
        if (machinePlan.getAvailableTime().isAvailable()) {
            var pair = assembleAvailableTimePeriodDTO(machinePlan.getAvailableTime().getAvailablePeriod(), now);
            dto.setAvailableTimePeriods(pair.getLeft());
            dto.setUnReachableTimePeriods(pair.getRight());
            if (null != totalSeconds) {
                dto.setWorkloadSaturation(Math.round(totalSeconds - machinePlan.getAvailableTime().getTotalSeconds()) / (double) totalSeconds);
            }
        } else {
            dto.setWorkloadSaturation(1.0);
        }
        if (CollectionUtils.isNotEmpty(machinePlan.getTaskList())) {
            dto.setOccupiedTimePeriods(assembleMachinePlannedPeriodDTO(machinePlan));
        }
        return dto;
    }

    private void fillAvailableTimePeriods(List<TimePeriodDTO> availableTimePeriods,
                                          List<TimePeriodDTO> unreachableTimePeriods,
                                          List<TimePeriod> periods, LocalDateTime now) {
        for (var period : periods) {
            var pair = period.splitTimePeriodByNow(now);
            if (null != pair.getLeft()) {
                unreachableTimePeriods.add(timePeriodAssembler.assembleTimePeriodDTOFromUnReachable(pair.getLeft()));
            }
            if (null != pair.getRight()) {
                availableTimePeriods.add(timePeriodAssembler.assembleTimePeriodDTO(pair.getRight()));
            }
        }
    }

    private List<MachinePlannedPeriodDTO> assembleMachinePlannedPeriodDTO(MachinePlan machinePlan) {
        var taskMap = machinePlan.getTaskList().stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it, (v1, v2) -> v1));
        var segments = machinePlan.expandPlannedTasks();

        List<MachinePlannedPeriodDTO> result = Lists.newArrayListWithExpectedSize(segments.size());
        for (var segment : segments) {
            var dto = new MachinePlannedPeriodDTO();
            var task = taskMap.get(segment.getTaskId().value());
            if (null != task) {
                dto.setStyleCode(task.getStyleCode().value());
                dto.setSize(task.getSize().value());
                dto.setPart(task.getPart().value());
                dto.setOrderCode(task.getProduceOrderCode().value());
                if (null != task.getEstimateEndTime()) {
                    dto.setEstimateEndTime(TimeUtil.formatGeneralString(task.getEstimateEndTime().value()));
                }
                dto.setWeavingPartOrderId(task.getWeavingPartOrderId().value());
                dto.setProduceQuantity(task.getProduceQuantity().getValue());
            }
            dto.setTaskId(segment.getTaskId().value());
            dto.setSegmentId(segment.getId().value());
            dto.setPlannedQuantity(segment.getPlan().getQuantity().getValue());
            if (null != segment.getExecution()) {
                dto.setProduceQuantity(segment.getExecution().getQuantity().getValue());
            }
            dto.setTimePeriod(assembleTimePeriodDTO(segment.getPlan().getPeriod()));
            dto.setStatus(segment.getStatus().getCode());

            result.add(dto);
        }
        return result;
    }

    public ProducePlanTrackDTO assembleProducePlanTrackDTO(OrderProduction orderProduction) {
        var dto = new ProducePlanTrackDTO();
        var produceOrder = orderProduction.getProduceOrder();
        dto.setProduceOrderId(produceOrder.getId().value());
        dto.setProduceOrderCode(produceOrder.getCode().value());
        dto.setDeliveryDate(TimeUtil.formatYYYYMMDD(produceOrder.getDeliveryTime().value()));
        dto.setLeftDays((int) TimeUtil.daysLeft(produceOrder.getDeliveryTime().value()));
        dto.setTotalQuantity(produceOrder.getTotalQuantity());
        if (CollectionUtils.isNotEmpty(orderProduction.getProductionList())) {
            dto.setStylePlanTrackList(assembleStylePlanTrackDTOList(orderProduction.getProductionList()));
        }
        return dto;
    }

    private List<WeavePartPlanTrackDTO> assembleStylePlanTrackDTOList(List<WeaveProduceTrack> productionList) {
        List<WeavePartPlanTrackDTO> res = Lists.newArrayListWithExpectedSize(productionList.size());
        for (var production : productionList) {
            res.add(assembleStylePlanTrackDTO(production));
        }
        return res;
    }

    private WeavePartPlanTrackDTO assembleStylePlanTrackDTO(WeaveProduceTrack production) {
        var dto = new WeavePartPlanTrackDTO();
        dto.setWeavingPartOrderId(production.getWeavingPartOrder().getId().value());
        dto.setStyleComponent(styleAssembler.assembleStyleComponentDTO(production.getComponent()));
        dto.setTotalQuantity(production.getPlanQuantity().getValue());
        dto.setProducedQuantity(production.getTillTodayQuantity().getValue());
        dto.setLeftQuantity(production.getLeftQuantity().getValue());
        if (null != production.getWeavingPartOrder().getPlannedQuantity()) {
            dto.setPlannedQuantity(production.getWeavingPartOrder().getPlannedQuantity().getValue());
            dto.setUnPlannedQuantity(production.getWeavingPartOrder().unPlannedQuantity());
        }
        dto.setArrangedMachineNum(production.getMachineNumber().value());
        var component = production.getComponent();
        dto.setDailyTheoreticalQuantity(component.getDailyTheoreticalQuantity().getValue());
        dto.setLeftDays(production.calculateLeftDays());
        return dto;
    }

    public ProducePredictDTO assembleProducePredictDTO(List<StyleComponentPredict> predicts, List<YarnUsage> totalYarnUsage) {
        var dto = new ProducePredictDTO();
        
        // 组装 styleProducePredicts
        var styleProducePredicts = predicts.stream()
                .map(predict -> {
                    var styleDto = new StyleProducePredictDTO();
                    styleDto.setStyleComponent(styleAssembler.assembleStyleComponentDTO(predict.getComponent()));
                    styleDto.setPrevDayProduction(predict.getDailyQuantity().getValue());
                    styleDto.setPredictProduction(predict.getPredictNextQuantity().getValue());
                    
                    // 组装当前预测的纱线需求
                    var yarnPredicts = predict.getTotalYarnUsage().stream()
                            .map(yarnUsage -> {
                                var yarnDto = new YarnPredictDTO();
                                yarnDto.setDemand(styleAssembler.convertToYarnUsageDTO(yarnUsage));
                                return yarnDto;
                            })
                            .collect(Collectors.toList());
                    styleDto.setYarnPredicts(yarnPredicts);
                    return styleDto;
                })
                .collect(Collectors.toList());
        dto.setStyleProducePredicts(styleProducePredicts);
        
        // 组装 yarnPredicts - 全局纱线需求汇总
        var globalYarnPredicts = totalYarnUsage.stream()
                .map(yarnUsage -> {
                    var yarnDto = new YarnPredictDTO();
                    yarnDto.setDemand(styleAssembler.convertToYarnUsageDTO(yarnUsage));
                    return yarnDto;
                })
                .collect(Collectors.toList());
        dto.setYarnPredicts(globalYarnPredicts);
        
        return dto;
    }
}
