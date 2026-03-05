package com.santoni.iot.aps.application.support.assembler;

import com.santoni.iot.aps.application.support.dto.TimePeriodDTO;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.entity.UnReachableTimePeriod;
import org.springframework.stereotype.Component;

@Component
public class TimePeriodAssembler {

    public TimePeriodDTO assembleTimePeriodDTO(TimePeriod timePeriod) {
        return new TimePeriodDTO(TimeUtil.formatGeneralString(timePeriod.getStart().value()),
                TimeUtil.formatGeneralString(timePeriod.getEnd().value()),
                timePeriod.getSeconds());
    }

    public TimePeriodDTO assembleTimePeriodDTOFromUnReachable(UnReachableTimePeriod timePeriod) {
        return new TimePeriodDTO(TimeUtil.formatGeneralString(timePeriod.getStart().value()),
                TimeUtil.formatGeneralString(timePeriod.getEnd().value()),
                timePeriod.getSeconds());
    }
}
