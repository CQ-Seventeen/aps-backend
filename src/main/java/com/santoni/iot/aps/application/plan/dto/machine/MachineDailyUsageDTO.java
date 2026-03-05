package com.santoni.iot.aps.application.plan.dto.machine;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MachineDailyUsageDTO {

    private String date;

    private int totalCount;

    private int workingCount;

    public MachineDailyUsageDTO(String date) {
        this.date = date;
    }
}
