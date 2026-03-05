package com.santoni.iot.aps.application.schedule.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.schedule.ScheduleTaskApplication;
import com.santoni.iot.aps.application.schedule.assembler.ScheduleAssembler;
import com.santoni.iot.aps.application.schedule.assembler.ScheduleDTOAssembler;
import com.santoni.iot.aps.application.schedule.command.DoScheduleCommand;
import com.santoni.iot.aps.application.schedule.command.StyleWeaveDemandCommand;
import com.santoni.iot.aps.application.schedule.command.SubmitScheduleCommand;
import com.santoni.iot.aps.application.schedule.dto.AggregateMachineScheduleDTO;
import com.santoni.iot.aps.application.schedule.dto.MachineScheduleResultDTO;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskDTO;
import com.santoni.iot.aps.application.schedule.dto.ScheduleTaskResultDTO;
import com.santoni.iot.aps.application.schedule.query.PageScheduleTaskQuery;
import com.santoni.iot.aps.application.schedule.query.ScheduleTaskResultQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.plan.service.PlanDomainService;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskId;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.ScheduleTaskResult;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.StylePartWeaveDemand;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.context.ScheduleContext;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.context.SubmitTaskContext;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleTaskRepository;
import com.santoni.iot.aps.domain.schedule.service.ScheduleDomainService;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.GreedySolution;
import com.santoni.iot.aps.domain.schedule.solver.greedy.solver.GreedySolver;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleTaskApplicationImpl implements ScheduleTaskApplication {

    @Autowired
    private ScheduleDomainService scheduleDomainService;

    @Autowired
    private PlanDomainService planDomainService;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private ResourceDomainService resourceDomainService;

    @Autowired
    private GreedySolver greedySolver;

    @Autowired
    private ScheduleTaskRepository scheduleTaskRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private ScheduleAssembler scheduleAssembler;

    @Autowired
    private ScheduleDTOAssembler scheduleDTOAssembler;

    @Override
    public void submitScheduleTask(SubmitScheduleCommand command) {
        var context = prepareSubmitTaskContext(command);
        var task = ScheduleTask.newOf(context.machineIdList(), context.demandList(), context.timePeriod());
        scheduleTaskRepository.createScheduleTask(task);
    }

    @Override
    public void doScheduleSolve(DoScheduleCommand command) {
        var task = scheduleTaskRepository.findScheduleTaskById(new ScheduleTaskId(command.getTaskId()));
        if (null == task) {
            log.error("Do SolveSchedule task not exist, taskId:{}", command.getTaskId());
            return;
        }
        PlanContext.setInstituteId(task.getInstituteId().value());
        var context = prepareSolveScheduleContext(task);
//        var orSolution = scheduleDomainService.buildORSolution(context);
//
//        var config = new SolverConfig(solverConfig);
//        SolverFactory<ORSolution> solverFactory = SolverFactory.create(config);
//        Solver<ORSolution> solver = solverFactory.buildSolver();
        try {
            task.beginToProcess();
            scheduleTaskRepository.updateScheduleTask(task);
            var solution = greedySolver.solve(context);
            saveSolution(task, solution);
        } catch (Exception e) {
            log.error("OR solve schedule exception, task:{}", JacksonUtil.toJson(task), e);
        } finally {
            PlanContext.clear();
        }
    }

    @Override
    public ScheduleTaskResultDTO directRecommend(SubmitScheduleCommand command) {
        var context = prepareSubmitTaskContext(command);
        var task = ScheduleTask.newOf(context.machineIdList(), context.demandList(), context.timePeriod());
        var scheduleContext = prepareSolveScheduleContext(task);

        task.beginToProcess();
        log.info("Recommend, task:{}", JacksonUtil.toJson(task));
        var solution = greedySolver.solve(scheduleContext);
        ScheduleTaskResult result = new ScheduleTaskResult(solution.getPlanList(), "", "");
        task.finish(result);
        log.info("Recommend, task:{}", JacksonUtil.toJson(task));

        var existPlan = fetchExistMachinePlan(task, task.getTimePeriod());
        return buildMachineScheduleResult(existPlan,  task.getResult().getMachinePlanList(), task.getTimePeriod());
    }

    @Override
    public PageResult<ScheduleTaskDTO> pageQueryScheduleTask(PageScheduleTaskQuery query) {
        var search = scheduleAssembler.composeSearchScheduleTask(query);
        var pageRes = scheduleTaskRepository.pageQueryScheduleTask(search);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        var taskDTOList = pageRes.getData().stream().map(it -> scheduleDTOAssembler.assembleScheduleTaskDTO(it)).toList();
        return PageResult.fromPageData(taskDTOList, pageRes);
    }

    @Override
    public ScheduleTaskResultDTO getScheduleTaskResult(ScheduleTaskResultQuery query) {
        var task = scheduleTaskRepository.findScheduleTaskById(new ScheduleTaskId(query.getTaskId()));
        if (null == task) {
            return null;
        }
        if (null == task.getResult()) {
            return new ScheduleTaskResultDTO();
        }
        var timePeriod = determineTimePeriod(task, query);
        var existPlan = fetchExistMachinePlan(task, timePeriod);

        return buildMachineScheduleResult(existPlan, task.getResult().getMachinePlanList(), timePeriod);
    }

    private TimePeriod determineTimePeriod(ScheduleTask task, ScheduleTaskResultQuery query) {
        var startTime = null == query.getStartTime() ? task.getTimePeriod().getStart().value() :
                query.getStartTime().isBefore(task.getTimePeriod().getStart().value()) ? query.getStartTime() : task.getTimePeriod().getStart().value();
        var endTime = null == query.getEndTime() ? task.getTimePeriod().getEnd().value() :
                query.getEndTime().isAfter(task.getTimePeriod().getEnd().value()) ? query.getEndTime() : task.getTimePeriod().getEnd().value();
        return TimePeriod.of(startTime, endTime);
    }

    private List<MachinePlan> fetchExistMachinePlan(ScheduleTask task, TimePeriod timePeriod) {
        var taskMap = machineTaskRepository.listByMachines(task.getMachineIds(), timePeriod.getStart(), new PlanEndTime(timePeriod.getEnd().value()))
                .stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));
        var machineMap = machineRepository.listMachineById(task.getMachineIds())
                .stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it));
        List<MachinePlan> result = Lists.newArrayListWithExpectedSize(task.getMachineIds().size());
        for (var schedule : task.getResult().getMachinePlanList()) {
            var plannedTask = taskMap.get(schedule.getMachineId().value());
            result.add(MachinePlan.of(machineMap.get(schedule.getMachineId().value()), plannedTask));
            machineMap.remove(schedule.getMachineId().value());
        }
        for (var entry : machineMap.entrySet()) {
            result.add(MachinePlan.of(entry.getValue(), taskMap.get(entry.getKey())));
        }
        planDomainService.sortMachinePlanByCylinderAndStartTime(result);
        return result;
    }

    private ScheduleTaskResultDTO buildMachineScheduleResult(List<MachinePlan> machinePlanList,
                                                             List<ScheduleOnMachine> scheduleList,
                                                             TimePeriod period) {

        var machineMap = machinePlanList.stream().collect(Collectors.groupingBy(it -> it.getMachine().getMachineSize().getCylinderDiameter().value()));
        List<AggregateMachineScheduleDTO> aggrMachineResultList = Lists.newArrayListWithExpectedSize(machineMap.size());
        var scheduleMap = scheduleList.stream()
                .collect(Collectors.toMap(it -> it.getMachineId().value(), it -> it,  (v1, v2) -> v1));
        for (var entry : machineMap.entrySet()) {
            var dto = new AggregateMachineScheduleDTO();
            dto.setCylinderDiameter(entry.getKey());
            List<MachineScheduleResultDTO> scheduleResultDTOList = Lists.newArrayListWithExpectedSize(entry.getValue().size());
            for (var plan : entry.getValue()) {
                var schedule = scheduleMap.get(plan.getMachine().getId().value());
                if (null == schedule) {
                    plan.calculateAvailableTimePeriod(timePeriodService, period.getStart().value(), period.getEnd().value());
                    scheduleResultDTOList.add(scheduleDTOAssembler.assembleMachineScheduleResultDTO(null, plan, plan.getAvailableTime()));

                } else {
                    var availableTime = scheduleDomainService.calculateAvailableTimePeriod(plan, schedule, period);
                    scheduleResultDTOList.add(scheduleDTOAssembler.assembleMachineScheduleResultDTO(schedule, plan, availableTime));
                }
            }
            dto.setMachineScheduleList(scheduleResultDTOList);
            aggrMachineResultList.add(dto);
        }
        aggrMachineResultList.sort(Comparator.comparing(AggregateMachineScheduleDTO::getCylinderDiameter));
        return new ScheduleTaskResultDTO(aggrMachineResultList, TimeUtil.formatGeneralString(period.getStart().value()),
                TimeUtil.formatGeneralString(period.getEnd().value()));
    }

    private void saveSolution(ScheduleTask task, GreedySolution solution) {
        ScheduleTaskResult result = new ScheduleTaskResult(solution.getPlanList(), "", "");
        task.finish(result);
        scheduleTaskRepository.updateScheduleTask(task);
    }

    private ScheduleContext prepareSolveScheduleContext(ScheduleTask task) {
        var machineList = machineRepository.listMachineById(task.getMachineIds());
        var machinePlanList = fetchExistMachinePlan(machineList, task);

        var partOrderIds = task.getStyleDemandList().stream().map(StylePartWeaveDemand::getWeavingPartOrderId).toList();
        var partOrders = weavingOrderRepository.listWeavingPartOrderById(partOrderIds);

        var skuList = fetchSku(partOrders);

        var machineStylePair = resourceDomainService.pairMachineAndStyle(machineList, skuList);
        var componentMap = skuList.stream().collect(Collectors.groupingBy(it -> it.getProduceOrderCode().value(),
                Collectors.toMap(it -> it.getCode().value(), StyleSku::getComponentMap)
        ));
        return new ScheduleContext(task, task.getStyleDemandList(), machinePlanList, machineStylePair, componentMap, task.getTimePeriod());
    }

    private List<MachinePlan> fetchExistMachinePlan(List<Machine> machineList, ScheduleTask task) {
        var taskMap = machineTaskRepository.listByMachines(task.getMachineIds(), task.getTimePeriod().getStart(),
                        new PlanEndTime(task.getTimePeriod().getEnd().value()))
                .stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));

        List<MachinePlan> result = Lists.newArrayListWithExpectedSize(machineList.size());
        for (var machine : machineList) {
            var plannedTask = taskMap.get(machine.getId().value());
            result.add(MachinePlan.of(machine, plannedTask));
        }
        return result;
    }

    private SubmitTaskContext prepareSubmitTaskContext(SubmitScheduleCommand command) {
        var partOrderIds = command.getStyleDemandList().stream().map(it -> new WeavingPartOrderId(it.getWeavingPartOrderId())).toList();
        var partOrders = weavingOrderRepository.listWeavingPartOrderById(partOrderIds);
        if (CollectionUtils.isEmpty(partOrders)) {
            throw new IllegalArgumentException("款式需求无效");
        }
        var timePeriod = TimePeriod.of(command.getStartTime(), command.getEndTime());
        var demandList = composeWeaveDemand(command.getStyleDemandList(), partOrders, timePeriod);
        var machineIds = machineRepository.listMachineById(command.getMachineIds().stream().map(MachineId::new).toList())
                .stream().map(Machine::getId).toList();
        return new SubmitTaskContext(machineIds, demandList, timePeriod);
    }

    private List<StylePartWeaveDemand> composeWeaveDemand(List<StyleWeaveDemandCommand> commandList,
                                                          List<WeavingPartOrder> partOrderList,
                                                          TimePeriod timePeriod) {
        var orderMap = partOrderList.stream().collect(Collectors.toMap(it -> it.getId().value(), it -> it));
        List<StylePartWeaveDemand> res = Lists.newArrayListWithExpectedSize(commandList.size());
        for (var command : commandList) {
            var order = orderMap.get(command.getWeavingPartOrderId());
            if (null == order) {
                continue;
            }
            TimePeriod period;
            if (order.getDeliveryTime().isBefore(timePeriod)) {
                period = timePeriod;
            } else if (order.getDeliveryTime().isAfter(timePeriod)) {
                period = timePeriod;
            } else {
                period = TimePeriod.of(timePeriod.getStart().value(), order.getDeliveryTime().value());
            }
            res.add(StylePartWeaveDemand.of(order, period));
        }
        return res;
    }

    private List<StyleSku> fetchSku(List<WeavingPartOrder> partOrders) {
        Map<ProduceOrderCode, Set<SkuCode>> orderSkuMap = Maps.newHashMap();
        for (var partOrder : partOrders) {
            var skuCodeSet = orderSkuMap.get(partOrder.getProduceOrderCode());
            if (null == skuCodeSet) {
                Set<SkuCode> skuCodes = Sets.newHashSet(partOrder.getDemand().getSkuCode());
                orderSkuMap.put(partOrder.getProduceOrderCode(), skuCodes);
            } else {
                skuCodeSet.add(partOrder.getDemand().getSkuCode());
            }
        }

        List<StyleSku> res = Lists.newArrayList();
        for (var entry : orderSkuMap.entrySet()) {
            var skus = styleRepository.listStyleSkuByCode(entry.getKey(), entry.getValue(), true);
            if (CollectionUtils.isNotEmpty(skus)) {
                res.addAll(skus);
            }
        }

        return res;
    }
}
