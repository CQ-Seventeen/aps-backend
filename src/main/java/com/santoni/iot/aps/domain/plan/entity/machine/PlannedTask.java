package com.santoni.iot.aps.domain.plan.entity.machine;

import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProducePeriod;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.plan.constant.TaskStatus;
import com.santoni.iot.aps.domain.plan.entity.valueobj.*;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PlannedTask {

    private TaskId id;

    private TaskCode taskCode;

    private FactoryId factoryId;

    private ProduceOrderCode produceOrderCode;

    private WeavingPartOrderId weavingPartOrderId;

    private MachineId machineId;

    private StyleCode styleCode;

    private SkuCode skuCode;

    private Part part;

    private Symbol symbol;

    private Size size;

    private Color color;

    private ProductionPlan plan;

    private ProduceQuantity produceQuantity;

    private TaskStatus status;

    private ProducePeriod executePeriod;

    private EndTime estimateEndTime;

    private List<TaskSegment> segments;

    private TaskFlag taskFlag;

    public void applyChange(TaskFlag taskFlag) {
        this.taskFlag = taskFlag;
    }

    public BigDecimal occupyPercent(LocalDateTime start, LocalDateTime end) {
        return plan.getPeriod().percentOccupied(start, end);
    }

    public void freeUpTimePeriod(StartTime startTime, EndTime endTime) {
        if (plan.getPeriod().getEnd().value().isBefore(startTime.value())) {
            return;
        }
        if (plan.getPeriod().getStart().value().isAfter(endTime.value())) {

        }
    }

    public PlannedQuantity cancel() {
        if (this.status == TaskStatus.INIT) {
            this.status = TaskStatus.CANCEL;
            return this.plan.getQuantity();
        }
        if (this.status == TaskStatus.PRODUCING) {
            this.status = TaskStatus.SUSPEND;
            this.plan.suspend(new PlanEndTime(LocalDateTime.now()));
            return this.plan.getQuantity().minus(this.produceQuantity);
        }
        return null;
    }

    public void setProduceQuantity(ProduceQuantity produceQuantity) {
        this.produceQuantity = produceQuantity;
        if (this.status == TaskStatus.INIT) {
            this.status = TaskStatus.PRODUCING;
        }
        if (this.produceQuantity.achieve(this.plan.getQuantity())) {
            this.status = TaskStatus.FINISH;
        }
    }

    public void estimateEndTime(LocalDateTime baseLine, StyleComponent component) {
        var quantity = this.plan.getQuantity().left(this.produceQuantity);
        if (!quantity.isZero()) {
            var seconds = component.theoreticalSeconds(quantity);
            var estimateEnd = baseLine.plusSeconds(seconds);
            this.estimateEndTime = new EndTime(estimateEnd);
        }
    }

    public void splitTask(TaskSegmentId segmentId, PlannedQuantity remainQuantity, PlanEndTime curPlanEndTime, PlanStartTime nextPlanStartTime) {
        TaskSegment toSplitSegment;
        if (null == segmentId) {
            toSplitSegment = segments.get(0);
        } else {
            toSplitSegment = this.segments.stream().filter(it -> it.getId().value() == segmentId.value()).findFirst().orElse(null);
        }
        if (null == toSplitSegment) {
            throw new IllegalArgumentException("任务不存在,无法切分");
        }
        TaskSegment newSegment = toSplitSegment.split(remainQuantity, curPlanEndTime, nextPlanStartTime);
        long postponeSeconds = Duration.between(curPlanEndTime.value(), nextPlanStartTime.value()).getSeconds();
        for (TaskSegment seg : segments) {
            if (seg.getIndex().after(toSplitSegment.getIndex())) {
                seg.reAssignAfterSplit(postponeSeconds);
            }
        }
        segments.add(newSegment);
    }

    public long totalSeconds() {
        long totalSeconds = 0;
        for (var seg : segments) {
            totalSeconds += seg.getPlan().getPeriod().totalSeconds();
        }
        return totalSeconds;
    }

    public static PlannedTask newTask(FactoryId factoryId,
                                      ProduceOrderCode produceOrderCode,
                                      WeavingPartOrderId weavingPartOrderId,
                                      MachineId machineId,
                                      StyleCode styleCode,
                                      SkuCode skuCode,
                                      Part part,
                                      Symbol symbol,
                                      Size size,
                                      Color color,
                                      ProductionPlan plan) {
        var code = initCode(produceOrderCode, weavingPartOrderId, machineId);
        return new PlannedTask(null, code, factoryId, produceOrderCode, weavingPartOrderId, machineId, styleCode,
                skuCode, part, symbol, size, color, plan,
                ProduceQuantity.zero(), TaskStatus.INIT, null, null, null, null);
    }

    private static TaskCode initCode(ProduceOrderCode orderCode, WeavingPartOrderId weavingPartOrderId, MachineId machineId) {
        var code = orderCode.value() + "-" + weavingPartOrderId.value() + "-" + machineId.value() + TimeUtil.formatNow();
        return new TaskCode(code);
    }

    public void attachId(TaskId id) {
        this.id = id;
    }

    public void initSegment() {
        TaskSegment segment = TaskSegment.newSegment(this.id, this.plan, this.status, SortIndex.init());
        if (CollectionUtils.isEmpty(this.segments)) {
            this.segments = Lists.newArrayList();
        }
        this.segments.add(segment);
    }

    public PlannedTask(TaskId id,
                       TaskCode taskCode,
                       FactoryId factoryId,
                       ProduceOrderCode produceOrderCode,
                       WeavingPartOrderId weavingPartOrderId,
                       MachineId machineId,
                       StyleCode styleCode,
                       SkuCode skuCode,
                       Part part,
                       Symbol symbol,
                       Size size,
                       Color color,
                       ProductionPlan plan,
                       ProduceQuantity produceQuantity,
                       TaskStatus status,
                       ProducePeriod executePeriod,
                       EndTime estimateEndTime,
                       List<TaskSegment> segments,
                       TaskFlag taskFlag) {
        this.id = id;
        this.taskCode = taskCode;
        this.factoryId = factoryId;
        this.produceOrderCode = produceOrderCode;
        this.weavingPartOrderId = weavingPartOrderId;
        this.machineId = machineId;
        this.styleCode = styleCode;
        this.skuCode = skuCode;
        this.part = part;
        this.symbol = symbol;
        this.size = size;
        this.color = color;
        this.plan = plan;
        this.produceQuantity = produceQuantity;
        this.status = status;
        this.executePeriod = executePeriod;
        this.estimateEndTime = estimateEndTime;
        this.taskFlag = taskFlag;
        if (!CollectionUtils.isEmpty(segments)) {
            if (null == id && segments.stream().anyMatch(it -> null != it.getTaskId())) {
                throw new IllegalArgumentException("任务阶段与任务id不一致");
            }
            if (null != id && segments.stream().anyMatch(it -> id.value() != it.getTaskId().value())) {
                throw new IllegalArgumentException("任务阶段与任务id不一致");
            }
            segments.sort((seg1, seg2) -> seg1.getPlan().compareByStart(seg2.getPlan()));
        }
        this.segments = segments;
    }
}
