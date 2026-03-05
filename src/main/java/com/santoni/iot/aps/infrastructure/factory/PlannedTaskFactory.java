package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProducePeriod;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskCode;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskFlag;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.entity.machine.TaskSegment;
import com.santoni.iot.aps.domain.plan.entity.valueobj.SortIndex;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskSegmentId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import com.santoni.iot.aps.infrastructure.po.PlannedTaskPO;
import com.santoni.iot.aps.infrastructure.po.TaskSegmentPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlannedTaskFactory {

    @Autowired
    private PlanFactory planFactory;

    public PlannedTaskPO convertPlannedTaskEntityToPO(PlannedTask task) {
        var po = new PlannedTaskPO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != task.getTaskCode()) {
            po.setTaskCode(task.getTaskCode().value());
        }
        if (null != task.getFactoryId()) {
            po.setFactoryId(task.getFactoryId().value());
        }
        po.setProduceOrderCode(task.getProduceOrderCode().value());
        po.setWeavingPartOrderId(task.getWeavingPartOrderId().value());
        po.setMachineId(task.getMachineId().value());
        if (null != task.getStyleCode()) {
            po.setStyleCode(task.getStyleCode().value());
        }
        if (null != task.getSkuCode()) {
            po.setSkuCode(task.getSkuCode().value());
        }
        if (null != task.getSymbol()) {
            po.setSymbolId(task.getSymbol().id());
            po.setSymbol(task.getSymbol().value());
        }
        if (null != task.getSize()) {
            po.setSizeId(task.getSize().id());
            po.setSize(task.getSize().value());
        }
        if (null != task.getColor()) {
            po.setColorId(task.getColor().id());
            po.setColor(task.getColor().value());
        }
        po.setSkuCode(task.getSkuCode().value());
        po.setPartId(task.getPart().id());
        po.setPart(task.getPart().value());
        po.setProducedQuantity(task.getProduceQuantity().getValue());
        po.setPlannedQuantity(task.getPlan().getQuantity().getValue());
        po.setPlanStartTime(task.getPlan().getPeriod().getStart().value());
        po.setPlanEndTime(task.getPlan().getPeriod().getEnd().value());
        if (null != task.getExecutePeriod()) {
            po.setActualStartTime(task.getExecutePeriod().getStart().value());
            if (null != task.getExecutePeriod().getEnd()) {
                po.setActualEndTime(task.getExecutePeriod().getEnd().value());
            }
        }
        if (null != task.getEstimateEndTime()) {
            po.setEstimateEndTime(task.getEstimateEndTime().value());
        }
        po.setStatus(task.getStatus().getCode());
        if (null != task.getTaskFlag()) {
            po.setFlag(task.getTaskFlag().value());
        }
        return po;
    }

    public TaskSegmentPO convertTaskSegmentEntityToPO(TaskSegment segment) {
        var po = new TaskSegmentPO();
        po.setTaskId(segment.getTaskId().value());
        po.setPlannedQuantity(segment.getPlan().getQuantity().getValue());
        po.setPlanStartTime(segment.getPlan().getPeriod().getStart().value());
        po.setPlanEndTime(segment.getPlan().getPeriod().getEnd().value());
        po.setStatus(segment.getStatus().getCode());
        po.setSortIndex(segment.getIndex().getValue());
        return po;
    }

    public PlannedTask composePlannedTask(PlannedTaskPO po,
                                          List<TaskSegmentPO> segmentPOList) {
        ProducePeriod executePeriod = null;
        if (po.getActualStartTime() != null) {
            executePeriod = ProducePeriod.of(po.getActualStartTime(), po.getActualEndTime());
        }
        EndTime estimateEndTime = null;
        if (po.getEstimateEndTime() != null) {
            estimateEndTime = new EndTime(po.getEstimateEndTime());
        }
        return new PlannedTask(
                new TaskId(po.getId()),
                StringUtils.isNotBlank(po.getTaskCode()) ? new TaskCode(po.getTaskCode()) : null,
                null == po.getFactoryId() ? null : new FactoryId(po.getFactoryId()),
                new ProduceOrderCode(po.getProduceOrderCode()),
                new WeavingPartOrderId(po.getWeavingPartOrderId()),
                new MachineId(po.getMachineId()),
                new StyleCode(po.getStyleCode()),
                new SkuCode(po.getSkuCode()),
                new Part(po.getPartId(), po.getPart()),
                StringUtils.isNotBlank(po.getSymbol()) ? new Symbol(po.getSymbolId(), po.getSymbol()) : null,
                StringUtils.isNotBlank(po.getSize()) ? new Size(po.getSizeId(), po.getSize()) : null,
                StringUtils.isNotBlank(po.getColor()) ? new Color(po.getColorId(), po.getColor()) : null,
                planFactory.composeProductionPlan(po.getPlannedQuantity(),
                        po.getPlanStartTime(), po.getPlanEndTime()),
                ProduceQuantity.of(po.getProducedQuantity()),
                TaskStatus.getByCode(po.getStatus()),
                executePeriod,
                estimateEndTime,
                segmentPOList.stream().map(this::composeTaskSegment).collect(Collectors.toList()),
                StringUtils.isNotBlank(po.getFlag()) ? new TaskFlag(po.getFlag()) : null
        );
    }

    public TaskSegment composeTaskSegment(TaskSegmentPO po) {
        return new TaskSegment(
                new TaskSegmentId(po.getId()),
                new TaskId(po.getTaskId()),
                planFactory.composeProductionPlan(po.getPlannedQuantity(),
                        po.getPlanStartTime(), po.getPlanEndTime()),
                TaskStatus.getByCode(po.getStatus()),
                null,
                SortIndex.of(po.getSortIndex())
        );
    }
}
