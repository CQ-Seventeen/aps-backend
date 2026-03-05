package com.santoni.iot.aps.domain.execute.entity.valueobj;

import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;

import java.util.List;
import java.util.Map;

public record ToOperateRecords(List<MachineDailyProduction> newRecords,
                               List<MachineDailyProduction> toUpdateRecords,
                               List<MachineDailyProduction> toDeleteRecords,
                               Map<Long, AlteredQuantity> alterByUpdate) {

}
