package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.plan.constant.AssignFactoryType;
import com.santoni.iot.aps.domain.plan.constant.FactoryTaskStatus;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTask;
import com.santoni.iot.aps.domain.plan.entity.factory.FactoryTaskDetail;
import com.santoni.iot.aps.domain.plan.entity.valueobj.OccupiedDays;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.MachineNumber;
import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskDetailPO;
import com.santoni.iot.aps.infrastructure.po.FactoryPlannedTaskPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FactoryTaskFactory {

    public FactoryPlannedTaskPO convertPlannedTaskEntityToPO(FactoryTask task) {
        var po = new FactoryPlannedTaskPO();
        po.setInstituteId(PlanContext.getInstituteId());
        po.setProduceOrderId(task.getProduceOrderId().value());
        po.setProduceOrderCode(task.getProduceOrderCode().value());
        po.setFactoryId(task.getFactoryId().value());
        po.setType(task.getAssignType().getCode());
        po.setPlanStartTime(task.getTimePeriod().getStart().value());
        po.setPlanEndTime(task.getTimePeriod().getEnd().value());
        po.setStatus(task.getStatus().getCode());
        return po;
    }

    public FactoryPlannedTaskDetailPO convertTaskDetailEntityToPO(FactoryTaskDetail detail, TaskId taskId) {
        var po = new FactoryPlannedTaskDetailPO();
        po.setTaskId(taskId.value());
        po.setMachineNumber(detail.getMachineNumber().value());
        po.setOccupiedDays(detail.getOccupiedDays().value());
        po.setCylinderDiameter(detail.getCylinderDiameter().value());
        po.setPlanStartTime(detail.getPlanPeriod().getStart().value());
        po.setPlanEndTime(detail.getPlanPeriod().getEnd().value());
        return po;
    }

    public FactoryTask composePlannedTask(FactoryPlannedTaskPO po,
                                          List<FactoryPlannedTaskDetailPO> detailPOList) {
        return new FactoryTask(
                new TaskId(po.getId()),
                new ProduceOrderId(po.getProduceOrderId()),
                new ProduceOrderCode(po.getProduceOrderCode()),
                new FactoryId(po.getFactoryId()),
                PlanPeriod.of(po.getPlanStartTime(), po.getPlanEndTime()),
                AssignFactoryType.getByCode(po.getType()),
                FactoryTaskStatus.getByCode(po.getStatus()),
                CollectionUtils.isEmpty(detailPOList) ? Collections.emptyList() :
                        detailPOList.stream().map(this::composeFactoryTaskDetail).collect(Collectors.toList())
        );
    }

    public FactoryTaskDetail composeFactoryTaskDetail(FactoryPlannedTaskDetailPO po) {
        return new FactoryTaskDetail(
                new TaskId(po.getTaskId()),
                new MachineNumber(po.getMachineNumber()),
                new OccupiedDays(po.getOccupiedDays()),
                PlanPeriod.of(po.getPlanStartTime(), po.getPlanEndTime()),
                new CylinderDiameter(po.getCylinderDiameter())
        );
    }
}
