package com.santoni.iot.aps.domain.plan.entity.factory;

import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.plan.constant.AssignFactoryType;
import com.santoni.iot.aps.domain.plan.constant.FactoryTaskStatus;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanPeriod;
import com.santoni.iot.aps.domain.plan.entity.valueobj.TaskId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import lombok.Getter;

import java.util.List;

@Getter
public class FactoryTask {

    private TaskId taskId;

    private ProduceOrderId produceOrderId;

    private ProduceOrderCode produceOrderCode;

    private FactoryId factoryId;

    private PlanPeriod timePeriod;


    private AssignFactoryType assignType;

    private FactoryTaskStatus status;

    private List<FactoryTaskDetail> assignDetail;

    public FactoryTask(TaskId taskId,
                       ProduceOrderId produceOrderId,
                       ProduceOrderCode produceOrderCode,
                       FactoryId factoryId,
                       PlanPeriod timePeriod,
                       AssignFactoryType assignType,
                       FactoryTaskStatus status,
                       List<FactoryTaskDetail> assignDetail) {
        this.taskId = taskId;
        this.produceOrderId = produceOrderId;
        this.produceOrderCode = produceOrderCode;
        this.factoryId = factoryId;
        this.timePeriod = timePeriod;
        this.assignType = assignType;
        this.status = status;
        this.assignDetail = assignDetail;
    }
}
