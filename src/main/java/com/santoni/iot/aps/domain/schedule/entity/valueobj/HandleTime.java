package com.santoni.iot.aps.domain.schedule.entity.valueobj;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HandleTime {

    private LocalDateTime submitTime;

    private LocalDateTime beginToProcessTime;

    private LocalDateTime finishTime;

    public HandleTime(LocalDateTime submitTime, LocalDateTime beginToProcessTime, LocalDateTime finishTime) {
        this.submitTime = submitTime;
        this.beginToProcessTime = beginToProcessTime;
        this.finishTime = finishTime;
    }

    public void beginToProcess() {
        this.beginToProcessTime = LocalDateTime.now();
    }

    public void finish() {
        this.finishTime = LocalDateTime.now();
    }
}
