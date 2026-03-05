package com.santoni.iot.aps.application.plan.dto.track;

import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import com.santoni.iot.aps.application.plan.dto.machine.MachineTaskDetailDTO;
import lombok.Data;

import java.util.List;

@Data
public class WeavePartPlanTrackDTO {

    private long weavingPartOrderId;

    private StyleComponentDTO styleComponent;

    private int totalQuantity;

    private int producedQuantity;

    private int leftQuantity;

    private int unPlannedQuantity;

    private int plannedQuantity;

    private int arrangedMachineNum;

    private List<MachineTaskDetailDTO> taskList;

    private int dailyTheoreticalQuantity;

    private double leftDays;
}
