package com.santoni.iot.aps.application.plan.dto.machine;

import lombok.Data;

@Data
public class MachineTaskListDTO {

    private long taskId;

    private String taskCode;

    private long machineId;

    private String deviceId;

    private int cylinderDiameter;

    private String produceOrderCode;

    private long weavingPartOrderId;

    private String styleCode;

    private String skuCode;

    private String size;

    private String part;

    private String color;

    private String planStartTime;

    private String planEndTime;

    private int plannedQuantity;

    private int producedQuantity;

    private String executeStartTime;

    private String executeEndTime;

    private int status;

    private String flag;
}
