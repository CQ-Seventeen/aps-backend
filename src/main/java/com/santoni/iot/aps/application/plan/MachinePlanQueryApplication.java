package com.santoni.iot.aps.application.plan;

import com.santoni.iot.aps.application.plan.dto.machine.*;
import com.santoni.iot.aps.application.plan.query.*;
import com.santoni.iot.aps.application.support.dto.PageResult;

import java.util.List;

public interface MachinePlanQueryApplication {

    MachineLevelDetailPlanDTO machinePlanDetail(MachinePlanQuery query);

    MachineLevelDetailPlanDTO canWeaveMachinePlan(CanWeaveMachinePlanQuery query);

    MachineLevelDetailPlanDTO canScheduleMachinePlan(CanScheduleMachinePlanQuery query);

    List<MachineDailyUsageDTO> queryDailyMachineUsage(MachinePlanQuery query);

    List<AggregateMachinePlanDTO> queryFreeSoonMachine(FreeSoonMachinePlanQuery query);

    PageResult<MachineTaskListDTO> pageQueryMachineTask(MachineTaskPageQuery query);

    MachineTaskDetailDTO queryMachineTaskDetail(MachineTaskDetailQuery query);

    List<MachinePlanDetailDTO> adviceForComplement(ComplementAdviceQuery query);
}
