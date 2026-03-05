package com.santoni.iot.aps.adapter.plan.convertor;

import com.santoni.iot.aps.adapter.plan.request.machine.CanWeaveMachinePlanRequest;
import com.santoni.iot.aps.adapter.plan.request.machine.PageQueryMachineTaskRequest;
import com.santoni.iot.aps.adapter.plan.request.order.ProduceOrderPlanRequest;
import com.santoni.iot.aps.adapter.resource.convertor.MachineConvertor;
import com.santoni.iot.aps.application.order.query.ProduceOrderQuery;
import com.santoni.iot.aps.application.plan.query.CanWeaveMachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.MachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.MachineTaskPageQuery;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.adapter.plan.request.machine.MachinePlanRequest;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import org.apache.commons.lang3.StringUtils;

public class PlanConvertor {

    public static CanWeaveMachinePlanQuery convertCanWeaveMachineRequestToQuery(CanWeaveMachinePlanRequest request) {
        var query = new CanWeaveMachinePlanQuery();
        query.setWeavingPartOrderId(request.getWeavingPartOrderId());
        query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? null : TimeUtil.fromGeneralString(request.getStartTime()));
        query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? null : TimeUtil.fromGeneralString(request.getEndTime()));
        return query;
    }

    public static MachineTaskPageQuery convertPageMachineTaskRequestToQuery(PageQueryMachineTaskRequest request) {
        var query = new MachineTaskPageQuery();
        query.setProduceOrderCode(request.getProduceOrderCode());
        query.setStyleCode(request.getStyleCode());
        query.setStartTime(StringUtils.isBlank(request.getStartTime()) ? null : TimeUtil.fromGeneralString(request.getStartTime()));
        query.setEndTime(StringUtils.isBlank(request.getEndTime()) ? null : TimeUtil.fromGeneralString(request.getEndTime()));
        query.copyPageParamFromPageRequest(request);
        return query;
    }

    public static MachinePlanQuery convertMachinePlanRequestToQuery(MachinePlanRequest request) {
        var query = new MachinePlanQuery();
        MachineConvertor.fillMachineQueryWithRequest(query, request);
        var startTime = StringUtils.isBlank(request.getStartTime()) ? TimeUtil.getStartOfToday() : TimeUtil.fromGeneralString(request.getStartTime());
        var endTime = StringUtils.isBlank(request.getEndTime()) ? TimeUtil.getEndOf(startTime.plusDays(10)) : TimeUtil.fromGeneralString(request.getEndTime());
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        return query;
    }

    public static ProduceOrderQuery convertToProduceOrderQuery(ProduceOrderPlanRequest request) {
        var query = new ProduceOrderQuery();
        query.setDeliveryTimeStart(StringUtils.isBlank(request.getDeliveryStartTime()) ? null : TimeUtil.fromGeneralString(request.getDeliveryStartTime()));
        query.setDeliveryTimeEnd(StringUtils.isBlank(request.getDeliveryEndTime()) ? null : TimeUtil.fromGeneralString(request.getDeliveryEndTime()));
        query.setStatus(ProduceOrderStatus.getStatusByPlanStatus(request.getPlanStatus()));
        return query;
    }

}
