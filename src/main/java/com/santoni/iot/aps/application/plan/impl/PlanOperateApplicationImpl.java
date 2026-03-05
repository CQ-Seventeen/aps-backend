package com.santoni.iot.aps.application.plan.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.plan.PlanOperateApplication;
import com.santoni.iot.aps.application.plan.command.*;
import com.santoni.iot.aps.application.plan.context.SyncTaskContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.ProductionPlan;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.*;
import com.santoni.iot.aps.application.plan.context.AssignTaskContext;
import com.santoni.iot.aps.domain.plan.entity.valueobj.context.BatchAssignTaskContext;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleLog;
import com.santoni.iot.aps.domain.schedule.repository.ScheduleLogRepository;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.infrastructure.acl.TPErpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlanOperateApplicationImpl implements PlanOperateApplication {

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ScheduleLogRepository scheduleLogRepository;

    @Autowired
    private TPErpClient tpErpClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void manuallyAssignTask(ManuallyAssignTaskCommand command) {
        var context = prepareAssignTaskContext(command);

        var plan = ProductionPlan.of(PlannedQuantity.of(command.getPlannedQuantity()),
                PlanPeriod.of(command.getPlanStartTime(), command.getPlanEndTime()));
        var task = context.weavingPartOrder().arrangeToMachine(context.machinePlan(), plan);
        transactionTemplate.execute(status -> {
            log.info("manually assign task save task transaction begin");
            try {
                var taskId = machineTaskRepository.createTask(task);
                var segment = TaskSegment.newSegment(taskId, plan, TaskStatus.INIT, SortIndex.init());
                machineTaskRepository.createTaskSegment(segment);
                weavingOrderRepository.updatePartOrder(context.weavingPartOrder());
                scheduleLogRepository.logSchedule(ScheduleLog.fromPlannedTask(taskId, task));

                task.attachId(taskId);
                log.info("manually assign task save task transaction success");
                return 1;
            } catch (Exception e) {
                log.error("manually assign task save task transaction exception", e);
                status.setRollbackOnly();
                return -1;
            }
        });

        CompletableFuture.supplyAsync(() -> {
            syncTask(task, context);
            return null;
        }).exceptionally(ex -> {
            log.error("Sync task to ERP error", ex);
            return null;
        });
    }

    private void syncTask(PlannedTask task, AssignTaskContext context) {
        var produceOrder = produceOrderRepository.produceOrderDetailById(context.weavingPartOrder().getProduceOrderId());
        if (null == produceOrder) {
            log.error("Sync Task to ERP error, no ProduceOrder found, {}", JacksonUtil.toJson(task));
        }
        var syncContext = new SyncTaskContext(context.weavingPartOrder(), context.machinePlan().getMachine(), context.styleComponent(), produceOrder, task);
        tpErpClient.syncPlanTask(syncContext);

        //todo record result retry
    }

    @Override
    public void updateTask(UpdateTaskCommand command) {
        var task = machineTaskRepository.taskDetailById(new TaskId(command.getTaskId()));
        if (null == task) {
            throw new IllegalArgumentException("task not exist");
        }
        if (StringUtils.isNotBlank(command.getTaskFlag())) {
            task.applyChange(new TaskFlag(command.getTaskFlag()));
            machineTaskRepository.saveTask(task, false);
        }
    }

    @Override
    public void splitTask(SplitTaskCommand command) {

    }

    @Override
    public void freeUpTime(FreeUpTimeCommand command) {
        var taskList = machineTaskRepository.listByMachines(command.getMachineIds()
                .stream().map(MachineId::new).toList(), new StartTime(command.getStartTime()), null);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

    }

    @Override
    public void batchAssignTask(BatchAssignTaskCommand command) {
        var context = prepareBatchAssignTaskContext(command);

        List<PlannedTask> newTasks = Lists.newArrayList();
        for (var machine : command.getMachineAssignList()) {
            var plan = context.planMap().get(machine.getMachineId());
            if (null == plan) {
                throw new IllegalArgumentException("Machine " + machine.getMachineId() + " not found");
            }
            for (var task : machine.getAssignList()) {
                var partOrder = context.partOrderMap().get(task.getWeavingPartOrderId());
                if (null == partOrder) {
                    throw new IllegalArgumentException("Part order " + task.getWeavingPartOrderId() + " not found");
                }
                var producePlan = ProductionPlan.of(PlannedQuantity.of(task.getQuantity()),
                        PlanPeriod.of(task.getStartTime(), task.getEndTime()));
                newTasks.add(partOrder.arrangeToMachine(plan, producePlan));
            }
        }

        transactionTemplate.execute(status -> {
            log.info("batch assign task save tasks transaction begin");
            try {
                for (var task : newTasks) {
                    var taskId = machineTaskRepository.createTask(task);
                    var segment = TaskSegment.newSegment(taskId, task.getPlan(), TaskStatus.INIT, SortIndex.init());
                    machineTaskRepository.createTaskSegment(segment);
                }
                for (var order : context.partOrderMap().values()) {
                    weavingOrderRepository.updatePartOrder(order);
                }
                log.info("batch assign task save tasks transaction success");
                return 1;
            } catch (Exception e) {
                log.error("batch assign task save tasks transaction exception", e);
                status.setRollbackOnly();
                return -1;
            }
        });
    }

    @Override
    public void realtimeAdjustTask() {

    }

    @Override
    public void cancelTask(CancelTaskCommand command) {
        var task = machineTaskRepository.taskDetailById(new TaskId(command.getTaskId()));
        if (null == task) {
            throw new IllegalArgumentException("task not exist");
        }
        var partOrder = weavingOrderRepository.getPartOrderById(task.getWeavingPartOrderId());
        if (null == partOrder) {
            throw new IllegalArgumentException("Part order " + task.getWeavingPartOrderId() + " not found");
        }
        var quantity = task.cancel();
        transactionTemplate.execute(status -> {
            try {
                machineTaskRepository.saveTask(task, false);
                if (null != quantity) {
                    partOrder.returnPlannedQuantity(quantity);
                    weavingOrderRepository.updatePartOrder(partOrder);
                }
                return 1;
            } catch (Exception e) {
                log.error("cancel task transaction exception", e);
                status.setRollbackOnly();
                return -1;
            }
        });
    }

    @Override
    public void manualSyncTask(long taskId) {
        var task = machineTaskRepository.taskDetailById(new TaskId(taskId));
        if (null == task) {
            return;
        }
        var partOrder = weavingOrderRepository.getPartOrderById(task.getWeavingPartOrderId());
        if (null == partOrder) {
            return;
        }
        var produceOrder = produceOrderRepository.produceOrderDetailById(partOrder.getProduceOrderId());
        if (null == produceOrder) {
            return;
        }
        var machine = machineRepository.detailById(task.getMachineId());
        if (null == machine) {
            return;
        }
        var styleComponent = styleRepository.getComponentBySkuAndPart(partOrder.getProduceOrderCode(), partOrder.getDemand().getSkuCode(), partOrder.getDemand().getPart());
        if (null == styleComponent) {
            return;
        }
        var syncContext = new SyncTaskContext(partOrder, machine, styleComponent, produceOrder, task);
        tpErpClient.syncPlanTask(syncContext);
    }

    private AssignTaskContext prepareAssignTaskContext(ManuallyAssignTaskCommand cmd) {
        var weavingPartOrder = weavingOrderRepository.getPartOrderById(new WeavingPartOrderId(cmd.getWeavingPartOrderId()));
        if (null == weavingPartOrder) {
            throw new IllegalArgumentException("织造部件单不存在");
        }
        var styleComponent = styleRepository.getComponentBySkuAndPart(weavingPartOrder.getProduceOrderCode(), weavingPartOrder.getDemand().getSkuCode(), weavingPartOrder.getDemand().getPart());
        if (null == styleComponent) {
            throw new IllegalArgumentException("款式不存在");
        }
        var machine = machineRepository.detailById(new MachineId(cmd.getMachineId()));
        if (null == machine) {
            throw new IllegalArgumentException("机器不存在");
        }
        var taskList = machineTaskRepository.findByMachine(machine.getId(), new StartTime(cmd.getPlanStartTime()), new PlanEndTime(cmd.getPlanEndTime()));
        var machinePlan = MachinePlan.of(machine, taskList);
        return new AssignTaskContext(weavingPartOrder, machinePlan, styleComponent);
    }

    private BatchAssignTaskContext prepareBatchAssignTaskContext(BatchAssignTaskCommand cmd) {
        Set<WeavingPartOrderId> partOrderIdSet = Sets.newHashSet();
        Set<MachineId> machineIdSet = Sets.newHashSet();

        for (var machineAssign : cmd.getMachineAssignList()) {
            machineIdSet.add(new MachineId(machineAssign.getMachineId()));
            if (CollectionUtils.isNotEmpty(machineAssign.getAssignList())) {
                partOrderIdSet.addAll(machineAssign.getAssignList().stream().map(it -> new WeavingPartOrderId(it.getWeavingPartOrderId())).toList());
            }
        }

        var partOrders = weavingOrderRepository.listWeavingPartOrderById(partOrderIdSet);
        if (CollectionUtils.isEmpty(partOrders) || partOrders.size() != partOrderIdSet.size()) {
            throw new IllegalArgumentException("部件单不存在");
        }
        var machines = machineRepository.listMachineById(machineIdSet);
        if (CollectionUtils.isEmpty(machines) || machines.size() != machineIdSet.size()) {
            throw new IllegalArgumentException("机器不存在");
        }
        var taskMap = machineTaskRepository.listByMachines(machineIdSet, new StartTime(cmd.getStartTime()), new PlanEndTime(cmd.getStartTime()))
                .stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));
        Map<Long, MachinePlan> planMap = Maps.newHashMapWithExpectedSize(machines.size());
        for (var machine : machines) {
            var task = taskMap.get(machine.getId().value());
            planMap.put(machine.getId().value(), MachinePlan.of(machine, task));
        }
        return new BatchAssignTaskContext(partOrders.stream()
                .collect(Collectors.toMap(it -> it.getId().value(), it -> it)),
                planMap);
    }

}
