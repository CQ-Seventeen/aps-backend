package com.santoni.iot.aps.domain.schedule.service;

import com.santoni.iot.aps.domain.plan.entity.machine.MachinePlan;
import com.santoni.iot.aps.domain.schedule.entity.valueobj.context.ScheduleContext;
import com.santoni.iot.aps.domain.schedule.entity.ScheduleOnMachine;
import com.santoni.iot.aps.domain.schedule.solver.or.solution.ORSolution;
import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;

public interface ScheduleDomainService {

    ORSolution buildORSolution(ScheduleContext context);

    AvailableTime calculateAvailableTimePeriod(MachinePlan existPlan, ScheduleOnMachine scheduleOnMachine, TimePeriod timePeriod);
}
