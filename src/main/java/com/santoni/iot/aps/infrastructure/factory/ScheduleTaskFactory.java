package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineId;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleTask;
import com.santoni.iot.aps.domain.schedule.entity.StyleWeavePlan;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.entity.valueobj.InstituteId;
import com.santoni.iot.aps.infrastructure.po.ScheduleTaskPO;
import com.santoni.iot.aps.infrastructure.po.assistance.ScheduleOnMachinePO;
import com.santoni.iot.aps.infrastructure.po.assistance.ScheduleResultPO;
import com.santoni.iot.aps.infrastructure.po.assistance.StyleWeaveDemandPO;
import com.santoni.iot.aps.infrastructure.po.assistance.StyleWeaveSchedulePO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleTaskFactory {

    public ScheduleTaskPO convertToScheduleTaskPO(ScheduleTask scheduleTask) {
        var po = new ScheduleTaskPO();
        po.setInstituteId(PlanContext.getInstituteId());
        var machineIds = scheduleTask.getMachineIds().stream().map(MachineId::value).toList();
        po.setMachineIds(JacksonUtil.toJson(machineIds));
        var demandPOList = scheduleTask.getStyleDemandList().stream().map(this::convertToStyleWeaveDemandPO).toList();
        po.setStyleDemands(JacksonUtil.toJson(demandPOList));
        po.setStartTime(scheduleTask.getTimePeriod().getStart().value());
        po.setEndTime(scheduleTask.getTimePeriod().getEnd().value());
        po.setStatus(scheduleTask.getStatus().getCode());
        if (null != scheduleTask.getResult()) {
            po.setScheduleResult(JacksonUtil.toJson(convertToScheduleResultPO(scheduleTask.getResult())));
        }
        if (null != scheduleTask.getHandleTime()) {
            po.setBeginProcessTime(scheduleTask.getHandleTime().getBeginToProcessTime());
            po.setFinishTime(scheduleTask.getHandleTime().getFinishTime());
        }
        return po;
    }

    public ScheduleTask composeScheduleTask(ScheduleTaskPO po,
                                            boolean needResultDetail) {
        var demandList = JacksonUtil.readAsObjList(po.getStyleDemands(), StyleWeaveDemandPO.class)
                .stream().map(this::composeStyleWeaveDemand).toList();
        var result = composeScheduleTaskResult(po, needResultDetail);
        return new ScheduleTask(new ScheduleTaskId(po.getId()),
                new InstituteId(po.getInstituteId()),
                JacksonUtil.readAsObjList(po.getMachineIds(), Long.class).stream().map(MachineId::new).toList(),
                demandList,
                TimePeriod.of(po.getStartTime(), po.getEndTime()),
                SubmitTaskStatus.findByCode(po.getStatus()),
                result,
                new HandleTime(po.getCreateTime(), po.getBeginProcessTime(), po.getFinishTime())
                );
    }

    public ScheduleTaskResult composeScheduleTaskResult(ScheduleTaskPO po, boolean needResultDetail) {
        if (StringUtils.isBlank(po.getScheduleResult())) {
            return null;
        }
        var resultPO = JacksonUtil.readAsObj(po.getScheduleResult(), ScheduleResultPO.class);
        if (null == resultPO) {
            return null;
        }
        if (!needResultDetail) {
            return new ScheduleTaskResult(List.of(), resultPO.getSuggestion(), resultPO.getErrorInfo());
        }
        var machineScheduleList = resultPO.getMachinePlanList()
                .stream()
                .map(this::composeScheduleOnMachine)
                .toList();
        return new ScheduleTaskResult(machineScheduleList, resultPO.getSuggestion(), resultPO.getErrorInfo());
    }

    private ScheduleOnMachine composeScheduleOnMachine(ScheduleOnMachinePO po) {
        var styleWeavePlanList = po.getStyleWeaveScheduleList()
                .stream()
                .map(this::composeStyleWeavePlan)
                .toList();
        return new ScheduleOnMachine(
                new MachineId(po.getMachineId()),
                styleWeavePlanList
        );
    }

    private StyleWeavePlan composeStyleWeavePlan(StyleWeaveSchedulePO po) {
        return new StyleWeavePlan(new WeavingPartOrderId(po.getWeavingPartOrderId()),
                new ProduceOrderCode(po.getOrderCode()),
                new SkuCode(po.getSkuCode()),
                new Part(po.getPart()),
                Quantity.of(po.getQuantity()),
                TimePeriod.of(po.getStartTime(), po.getEndTime()));
    }

    private StylePartWeaveDemand composeStyleWeaveDemand(StyleWeaveDemandPO po) {
        return new StylePartWeaveDemand(
                new WeavingPartOrderId(po.getWeavingPartOrderId()),
                new ProduceOrderCode(po.getOrderCode()),
                new SkuCode(po.getSkuCode()),
                new Part(po.getPart()),
                TimePeriod.of(TimeUtil.fromGeneralString(po.getStartTime()), TimeUtil.fromGeneralString(po.getEndTime())),
                Quantity.of(po.getQuantity())
        );
    }

    private StyleWeaveDemandPO convertToStyleWeaveDemandPO(StylePartWeaveDemand demand) {
        var po = new StyleWeaveDemandPO();
        po.setWeavingPartOrderId(demand.getWeavingPartOrderId().value());
        po.setOrderCode(demand.getOrderCode().value());
        po.setSkuCode(demand.getSkuCode().value());
        po.setPart(demand.getPart().value());
        po.setQuantity(demand.getQuantity().getValue());
        po.setStartTime(TimeUtil.formatGeneralString(demand.getTimePeriod().getStart().value()));
        po.setEndTime(TimeUtil.formatGeneralString(demand.getTimePeriod().getEnd().value()));
        return po;
    }

    private ScheduleResultPO convertToScheduleResultPO(ScheduleTaskResult result) {
        var po = new ScheduleResultPO();
        po.setErrorInfo(result.getErrorInfo());
        po.setSuggestion(result.getSuggestion());
        po.setMachinePlanList(result.getMachinePlanList().stream().map(this::convertToScheduleOnMachinePO).toList());
        return po;
    }

    private ScheduleOnMachinePO convertToScheduleOnMachinePO(ScheduleOnMachine scheduleOnMachine) {
        var po = new ScheduleOnMachinePO();
        po.setMachineId(scheduleOnMachine.getMachineId().value());
        po.setStyleWeaveScheduleList(scheduleOnMachine.getStyleWeavePlanList().stream().map(it -> {
            var styleWeaveSchedulePO = new StyleWeaveSchedulePO();
            styleWeaveSchedulePO.setWeavingPartOrderId(it.getWeavingPartOrderId().value());
            styleWeaveSchedulePO.setOrderCode(it.getOrderCode().value());
            styleWeaveSchedulePO.setSkuCode(it.getSkuCode().value());
            styleWeaveSchedulePO.setPart(it.getPart().value());
            styleWeaveSchedulePO.setQuantity(it.getQuantity().getValue());
            styleWeaveSchedulePO.setStartTime(it.getTimePeriod().getStart().value());
            styleWeaveSchedulePO.setEndTime(it.getTimePeriod().getEnd().value());
            return styleWeaveSchedulePO;
        }).toList());
        return po;
    }
}
