package com.santoni.iot.aps.adapter.execute.job;

import com.santoni.iot.aps.application.execute.ProduceOperateApplication;
import com.santoni.iot.aps.application.execute.command.WriteProductionToTaskCommand;
import com.santoni.iot.aps.common.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ProductionJob {

    @Autowired
    private ProduceOperateApplication produceOperateApplication;

    @Scheduled(cron = "0 0,30 7-22 * * ?")
    public void writeBackProduction() {
        log.info("Begin WriteBack task");
        long start = System.currentTimeMillis();

        var date = TimeUtil.formatYYYYMMDD(LocalDateTime.now().minusDays(1));
        var command = new WriteProductionToTaskCommand();
        command.setDate(date);
        produceOperateApplication.writeProductionToTask(command);
        log.info("WriteBack finish, consume:{}", System.currentTimeMillis() - start);
    }

    @Scheduled(cron = "0 0,30 7-22 * * ?")
    public void summaryProduction() {
        log.info("Begin SummaryProduction task");
        long start = System.currentTimeMillis();
        produceOperateApplication.sumProduction();
        log.info("SummaryProduction finish, consume:{}", System.currentTimeMillis() - start);
    }
}
