package com.santoni.iot.aps.application.plan.dto.machine;

import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import com.santoni.iot.aps.application.resource.dto.MachineDTO;
import lombok.Data;

@Data
public class MachineTaskDetailDTO {

    private long taskId;

    private String taskCode;

    private MachineDTO machine;

    private String produceOrderCode;

    private String deliveryDate;

    private long weavingPartOrderId;

    private String styleCode;

    private String styleName;

    private String symbolId;

    private String symbol;

    private String sizeId;

    private String size;

    private String skuCode;

    private String colorId;

    private String color;

    private String partId;

    private String part;

    private StyleComponentDTO styleComponent;

    private String planStartTime;

    private String planEndTime;

    private int plannedQuantity;

    private int producedQuantity;

    private String executeStartTime;

    private String executeEndTime;

}
