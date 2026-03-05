package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachinePO extends BasePO {

    private Long id;

    private String deviceId;

    private String code;

    private long instituteId;

    private long factoryId;

    private long workshopId;

    private long machineGroupId;

    private Integer cylinderDiameter;

    private Integer needleSpacing;

    private Integer needleNumber;

    private String machineType;

    private String bareSpandexType;

    private boolean highSpeed;

    private Map<String, List<String>> features;

    private int status;

}
