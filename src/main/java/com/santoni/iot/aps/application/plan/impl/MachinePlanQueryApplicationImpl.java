package com.santoni.iot.aps.application.plan.impl;

import com.santoni.iot.aps.application.plan.MachinePlanQueryApplication;
import com.santoni.iot.aps.application.plan.assembler.PlanAssembler;
import com.santoni.iot.aps.application.plan.assembler.PlanDTOAssembler;
import com.santoni.iot.aps.application.plan.context.BuildMachineTaskDetailContext;
import com.santoni.iot.aps.application.plan.dto.machine.*;
import com.santoni.iot.aps.application.plan.query.*;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.MachineUsage;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.plan.service.PlanDomainService;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MachinePlanQueryApplicationImpl implements MachinePlanQueryApplication {

    @Autowired
    private ResourceDomainService resourceDomainService;

    @Autowired
    private PlanDomainService planDomainService;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private PlanAssembler planAssembler;

    @Autowired
    private PlanDTOAssembler planDTOAssembler;

    @Override
    public MachineLevelDetailPlanDTO machinePlanDetail(MachinePlanQuery query) {
        var planList = getMachinePlanList(query);
        return assembleMachinePlanDetailDTO(planList, query.getStartTime(), query.getEndTime());
    }

    @Override
    public MachineLevelDetailPlanDTO canWeaveMachinePlan(CanWeaveMachinePlanQuery query) {
        var order = weavingOrderRepository.getPartOrderById(new WeavingPartOrderId(query.getWeavingPartOrderId()));
        if (null == order) {
            throw new IllegalArgumentException("织造单不存在,orderId:" + query.getWeavingPartOrderId());
        }
        var machineList = canWeaveMachine(order);
        var planList = planOfMachines(machineList, query.getStartTime(), query.getEndTime());
        return assembleMachinePlanDetailDTO(planList, query.getStartTime(), query.getEndTime());
    }

    @Override
    public MachineLevelDetailPlanDTO canScheduleMachinePlan(CanScheduleMachinePlanQuery query) {
        if (CollectionUtils.isEmpty(query.getWeavingPartOrderIds())) {
            throw new IllegalArgumentException("未选中有效部件单");
        }
        var partOrderList = weavingOrderRepository.listWeavingPartOrderById(
                query.getWeavingPartOrderIds().stream().map(WeavingPartOrderId::new).toList()
        );
        if (CollectionUtils.isEmpty(partOrderList)) {
            throw new IllegalArgumentException("织造单不存在:" + query.getWeavingPartOrderIds());
        }
        var machineList = canScheduleMachine(partOrderList);
        var startTime = LocalDateTime.now();
        var latestTime = partOrderList.stream().map(it -> it.getDeliveryTime().value()).max(LocalDateTime::compareTo);
        var endTime = latestTime.isEmpty() || latestTime.get().isBefore(startTime) ? TimeUtil.getStartOfDaysAfter(startTime, 10) : latestTime.get();
        var planList = planOfMachines(machineList, startTime, endTime);

        if (CollectionUtils.isEmpty(planList)) {
            return MachineLevelDetailPlanDTO.empty(startTime, endTime);
        }
        planDomainService.sortMachinePlanByCylinderAndStartTime(planList);
        return planDTOAssembler.assembleMachineLevelDetailPlanDTO(planList, startTime, endTime);
    }

    @Override
    public List<MachineDailyUsageDTO> queryDailyMachineUsage(MachinePlanQuery query) {
        var dateList = timePeriodService.extractDate(query.getStartTime(), query.getEndTime());
        var planList = getMachinePlanList(query);
        if (CollectionUtils.isEmpty(planList)) {
            return dateList.stream().map(it -> new MachineDailyUsageDTO(it.getDate())).toList();
        }
        var usage = MachineUsage.initFromDatePeriods(dateList);
        for (var plan : planList) {
            plan.calculateUsage(dateList, usage);
        }
        return planDTOAssembler.assembleMachineDailyUsageList(usage, dateList);
    }

    @Override
    public List<AggregateMachinePlanDTO> queryFreeSoonMachine(FreeSoonMachinePlanQuery query) {
        var searchMachine = planAssembler.composeSearchMachineFromFreeSoonQuery(query);
        var machineList = machineRepository.pageQueryMachine(searchMachine);
        if (CollectionUtils.isEmpty(machineList.getData())) {
            return Collections.emptyList();
        }
        var planList = planOfMachines(machineList.getData(), query.getStartTime(), query.getEndTime());
        if (CollectionUtils.isEmpty(planList)) {
            return Collections.emptyList();
        }
        planDomainService.sortMachinePlanListByEndTime(planList);
        var now = LocalDateTime.now();
        return planDTOAssembler.assembleAggregateMachinePlanList(planList, now, null);
    }

    @Override
    public PageResult<MachineTaskListDTO> pageQueryMachineTask(MachineTaskPageQuery query) {
        var search = planAssembler.composeSearchPlannedTask(query);
        var pageRes = machineTaskRepository.pageQueryTask(search);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        var context = prepareBuildTaskContext(pageRes.getData());
        var dtoList = pageRes.getData().stream().map(it -> planDTOAssembler.assembleMachineTaskDTO(it, context)).toList();
        return PageResult.fromPageData(dtoList, pageRes);
    }

    @Override
    public MachineTaskDetailDTO queryMachineTaskDetail(MachineTaskDetailQuery query) {
        var task = machineTaskRepository.taskDetailById(new TaskId(query.getTaskId()));
        if (null == task) {
            throw new IllegalArgumentException("机台计划不存在" + query.getTaskId());
        }
        var context = prepareMachineTaskDetailContext(task);

        return planDTOAssembler.assembleMachineTaskDetailDTO(task, context);
    }

    @Override
    public List<MachinePlanDetailDTO> adviceForComplement(ComplementAdviceQuery query) {

        return List.of();
    }

    private BuildMachineTaskDetailContext prepareMachineTaskDetailContext(PlannedTask task) {
        var machine = machineRepository.detailById(task.getMachineId());
        var styleComponent = styleRepository.getComponentBySkuAndPart(task.getProduceOrderCode(), task.getSkuCode(), task.getPart());
        var partOrder = weavingOrderRepository.getPartOrderById(task.getWeavingPartOrderId());
        var sku = styleRepository.getStyleSkuByCode(task.getProduceOrderCode(), task.getSkuCode(), false);
        var spu = styleRepository.getStyleByCode(task.getProduceOrderCode(), sku.getStyleCode());

        return new BuildMachineTaskDetailContext(machine, styleComponent, sku, spu, partOrder);
    }

    private MachineLevelDetailPlanDTO assembleMachinePlanDetailDTO(List<MachinePlan> planList,
                                                                   LocalDateTime startTime,
                                                                   LocalDateTime endTime) {
        if (CollectionUtils.isEmpty(planList)) {
            return MachineLevelDetailPlanDTO.empty(startTime, endTime);
        }
        planDomainService.sortMachinePlanListByStartTime(planList);
        return planDTOAssembler.assembleMachineLevelDetailPlanDTO(planList, startTime, endTime);
    }

    private Map<Long, Machine> prepareBuildTaskContext(List<PlannedTask> taskList) {
        var machineIdSet = taskList.stream().map(PlannedTask::getMachineId).collect(Collectors.toSet());
        return machineRepository.listMachineById(machineIdSet.stream().toList())
                .stream()
                .collect(Collectors.toMap(it -> it.getId().value(), it -> it));
    }

    private List<MachinePlan> getMachinePlanList(MachinePlanQuery query) {
        var search = planAssembler.composeSearchMachine(query);
        var machineList = machineRepository.pageQueryMachine(search);
        if (CollectionUtils.isEmpty(machineList.getData())) {
            return Collections.emptyList();
        }
        return planOfMachines(machineList.getData(), query.getStartTime(), query.getEndTime());
    }

    private List<Machine> canWeaveMachine(WeavingPartOrder partOrder) {
        var component = styleRepository.getComponentBySkuAndPart(partOrder.getProduceOrderCode(), partOrder.getDemand().getSkuCode(), partOrder.getDemand().getPart());
        if (null == component) {
            return Collections.emptyList();
        }
        var canWeaveOption = resourceDomainService.compatibleMachineByStyleComponent(component);
        return machineRepository.filterMachineByOption(canWeaveOption, partOrder.getFactoryId());
    }

    private List<Machine> canScheduleMachine(List<WeavingPartOrder> partOrderList) {
        var pairList = partOrderList.stream().map(it -> Pair.of(it.getDemand().getSkuCode(),
                it.getDemand().getPart())).toList();
        var componentList = styleRepository.listComponentBySkuAndPart(pairList);
        if (CollectionUtils.isEmpty(componentList)) {
            return Collections.emptyList();
        }
        var canWeaveOption = resourceDomainService.compatibleMachineByComponentList(componentList);
        return machineRepository.filterMachineByOption(canWeaveOption, partOrderList.get(0).getFactoryId());
    }

    private List<MachinePlan> planOfMachines(List<Machine> machineList, LocalDateTime startTime, LocalDateTime endTime) {
        var taskMap = machineTaskRepository.listByMachines(machineList.stream().map(Machine::getId).toList(), new StartTime(startTime), new PlanEndTime(endTime))
                .stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));
        return planDomainService.composeMachinePlanList(machineList, taskMap, startTime, endTime);
    }

}
