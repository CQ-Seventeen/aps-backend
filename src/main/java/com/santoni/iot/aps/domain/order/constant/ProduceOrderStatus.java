package com.santoni.iot.aps.domain.order.constant;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum ProduceOrderStatus {

    CREATED(0, "创建"),
    PLANNING_NO_PRODUCE(10, "计划中，未生产"),
    PLANNING_PRODUCING(11, "计划中，生产中"),
    PLANNED_NO_PRODUCE(20, "已排产，未生产"),
    PLANNED_PRODUCING(21, "已排产，生产中"),
    FINISHED(3, "完成"),
    CANCELLED(-1, "取消"),
    ;

    private int code;
    private String desc;

    private static final List<Integer> ALL_PLAN_STATUS = Lists.newArrayList(CREATED.code, PLANNING_NO_PRODUCE.code, PLANNING_PRODUCING.code, PLANNED_NO_PRODUCE.code, PLANNED_PRODUCING.code);
    private static final List<Integer> IN_PLAN_STATUS = Lists.newArrayList(CREATED.code, PLANNING_NO_PRODUCE.code, PLANNING_PRODUCING.code);
    private static final List<Integer> PLANNED_STATUS = Lists.newArrayList(PLANNED_NO_PRODUCE.code, PLANNED_PRODUCING.code);


    ProduceOrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProduceOrderStatus findByCode(int code) {
        for (ProduceOrderStatus status : ProduceOrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法的生产订单状态" + code);
    }

    public static List<Integer> getStatusByPlanStatus(Integer planStatus) {
        if (null == planStatus || planStatus == -1) {
            return ALL_PLAN_STATUS;
        }
        if (planStatus == 2) {
            return IN_PLAN_STATUS;
        }
        return PLANNED_STATUS;
    }

    public static List<Integer> getUnfinishedStatus() {
        return ALL_PLAN_STATUS;
    }
}
