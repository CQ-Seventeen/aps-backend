package com.santoni.iot.aps.infrastructure.acl.feign.request;

import lombok.Data;

@Data
public class WeavingScheduleItem {

    private int qty;

    private Integer fabricFactor;

    private Integer stdFrameQty;

    private String id;

    private String masId;

    private Integer seqNumber;

    private Integer seqCrossNumber;

    private String taskDtlId;

    private String prodOrderId;

    private String prodOrderNo;

    private String itemId;

    private String itemCode;

    private String itemName;

    private String docType;

    private String colorId;

    private String colorName;

    private String sizeId;

    private String sizeName;

    private String taskSourceDtlIds;

    private String styleId;

    private String styleName;

    private String fontId;

    private String fontName;

    private String unitId;

    private String unitName;

    private String partsId;

    private String partsName;

    private String memo;

    private String weavingRatioId;

    private String weavingRatioName;

    private Integer logoutTime;

    private String programName;

    private Double logoutWeight;

    private String logoutWidth;

    private String logoutHeight;

    private String dyeMemo;

    private String dyeIds;

    private String dyeingProcessId;

    private String dyeingProcessName;

    private String prodSchedulingDtlId;

    private String weavingSchedulingId;

    private String sysUniqueId;

    private String sourceId;

    private String custDeliveryDate;

    private String deliveryDate;

    private String cupCodeId;

    private String cupCodeName;

    private String mcTypeId;

    private String mcTypeName;

    private String mcSizeId;

    private String mcSizeName;

    private Integer groupQty;

    private Integer groupConesNum;

    private String scheduleBillNo;

    private String scheduleBillId;

    private Integer theoryYieldDay;

    private Double finishDays;

    private Integer groupTheoryDayYield;

    private String compId;

    private String compName;

    private String deptId;

    private String deptName;

    private Integer conesNum;

    private String mcId;

    private String mcName;

    private String startDate;

    private String endDate;
}

