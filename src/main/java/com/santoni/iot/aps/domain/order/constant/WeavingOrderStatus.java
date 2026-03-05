package com.santoni.iot.aps.domain.order.constant;

import lombok.Getter;

@Getter
public enum WeavingOrderStatus {

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

    WeavingOrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WeavingOrderStatus findByCode(int code) {
        for (WeavingOrderStatus status : WeavingOrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("不支持的织造单状态" + code);
    }

    public WeavingOrderStatus toggleStatusAfterPlan(boolean finishPlan) {
        if (this == WeavingOrderStatus.CREATED) {
            return finishPlan ? WeavingOrderStatus.PLANNED_NO_PRODUCE : WeavingOrderStatus.PLANNING_NO_PRODUCE;
        }
        if (finishPlan) {
            if (this == WeavingOrderStatus.PLANNING_NO_PRODUCE) {
                return WeavingOrderStatus.PLANNED_NO_PRODUCE;
            }
            if (this == WeavingOrderStatus.PLANNING_PRODUCING) {
                return WeavingOrderStatus.PLANNED_PRODUCING;
            }
        }
        return this;
    }

}
