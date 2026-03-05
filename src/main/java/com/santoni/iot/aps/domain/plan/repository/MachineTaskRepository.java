package com.santoni.iot.aps.domain.plan.repository;

import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.SearchPlannedTask;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;

import java.util.Collection;
import java.util.List;

public interface MachineTaskRepository {

    TaskId createTask(PlannedTask task);

    void createTaskSegment(TaskSegment segment);

    void saveTask(PlannedTask task, boolean saveSegment);

    List<PlannedTask> findByMachine(MachineId machineId, StartTime startTime, PlanEndTime endTime);

    List<PlannedTask> listByMachines(Collection<MachineId> machineIds, StartTime startTime, PlanEndTime endTime);

    List<PlannedTask> listByWeavingOrder(WeavingOrderId weavingOrderId);

    List<PlannedTask> listByTaskIds(List<TaskId> taskIds);

    PageData<PlannedTask> pageQueryTask(SearchPlannedTask search);

    List<PlannedTask> listByEndTime(List<MachineId> machineIds, EndTime endTimeStart, EndTime endTimeEnd);

    List<PlannedTask> listAllProcessingTask(FactoryId factoryId);

    PlannedTask taskDetailById(TaskId taskId);

    List<PlannedTask> listByProduceOrder(Collection<ProduceOrderCode> produceOrderCodes);

    List<PlannedTask> listByTime(StartTime startTime, EndTime endTime);

    PlannedTask find(MachineId machineId, ProduceOrderCode orderCode, StyleCode styleCode, Size size, Part part);

}
