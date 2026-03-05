package com.santoni.iot.aps.application.execute.impl;

import com.santoni.iot.aps.application.execute.ProduceOperateApplication;
import com.santoni.iot.aps.application.execute.command.RecordMachineProductionCommand;
import com.santoni.iot.aps.application.execute.command.WriteProductionToTaskCommand;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Size;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.execute.entity.MachineDailyProduction;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ReportBarCode;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.execute.repository.ManuallyReportExecuteRepository;
import com.santoni.iot.aps.domain.execute.service.ExecuteDomainService;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineDeviceId;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProduceOperateApplicationImpl implements ProduceOperateApplication {

    @Autowired
    private ExecuteDomainService executeDomainService;

    @Autowired
    private ManuallyReportExecuteRepository manuallyReportExecuteRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void removeRecord() {

    }

    @Override
    public void writeProductionToTask(WriteProductionToTaskCommand command) {
        PlanContext.setInstituteId(1L);
        handleTaskProduction();
        PlanContext.clear();
    }

    @Override
    public void sumProduction() {
        var curDay = LocalDateTime.now();
        sumByProductionDetail(curDay.minusDays(2));
        sumByProductionDetail(curDay.minusDays(1));
        sumByProductionDetail(curDay);
    }

    @Override
    public void reportProduction(RecordMachineProductionCommand command) {
        var record = manuallyReportExecuteRepository.findByBarCode(new ReportBarCode(command.getBarCode()));
        if (null != record) {
            if (command.getType() == 1) {
                record.changeQuantity(ProduceQuantity.of(command.getPieces()), ProduceQuantity.of(command.getDefectPieces()));
            } else {
                record.changeInspectQuantity(ProduceQuantity.of(command.getPieces()), ProduceQuantity.of(command.getDefectPieces()));
            }
            manuallyReportExecuteRepository.saveDailyProduction(record);
            return;
        }
        var machine = machineRepository.findByDeviceId(null, new MachineDeviceId(command.getDeviceId()));
        if (null == machine) {
            throw new IllegalArgumentException("机器不存在:" + command.getDeviceId());
        }
        var task = machineTaskRepository.find(machine.getId(), new ProduceOrderCode(command.getOrderCode()),
                new StyleCode(command.getStyleCode()), new Size(command.getSize()), new Part(command.getPart()));
        if (null == task) {
            throw new IllegalArgumentException("找不到计划单");
        }
        var newRecord = MachineDailyProduction.fromPlannedTask(new ProduceDate(command.getDate()), task, machine, new ReportBarCode(command.getBarCode()));
        newRecord.changeQuantity(ProduceQuantity.of(command.getPieces()), ProduceQuantity.of(command.getDefectPieces()));
        manuallyReportExecuteRepository.saveDailyProduction(newRecord);
    }

    private void sumByProductionDetail(LocalDateTime timeOfDate) {
        var date = TimeUtil.formatYYYYMMDD(timeOfDate);
        var produceDate = new ProduceDate(date);
        var productionDetail = manuallyReportExecuteRepository.listProductionByDate(produceDate, null);
        if (CollectionUtils.isEmpty(productionDetail)) {
            return;
        }
        var keyList = productionDetail.stream().map(MachineDailyProduction::getSumKey).distinct().map(SumKey::new).toList();

        var curSum = manuallyReportExecuteRepository.listProductionSumByKeyAndDate(produceDate, keyList, false);
        var prevDate = new ProduceDate(TimeUtil.formatYYYYMMDD(timeOfDate.minusDays(1)));
        var prevSum = manuallyReportExecuteRepository.listProductionSumByKeyAndDate(prevDate, keyList, false);
        var latestSum = manuallyReportExecuteRepository.listProductionSumByKeyAndDate(prevDate, keyList, true);

        var sumList = executeDomainService.summaryDayProduction(productionDetail, prevSum, latestSum, curSum);
        transactionTemplate.execute(status -> {
            try {
                for (var sum : sumList) {
                    manuallyReportExecuteRepository.saveProductionSum(sum);
                }
                return 1;
            } catch (Exception e) {
                log.error("Save productionSum  error, task:{}", JacksonUtil.toJson(sumList), e);
                status.setRollbackOnly();
                return -1;
            }
        });
    }

    private void handleTaskProduction() {
        var taskList = machineTaskRepository.listAllProcessingTask(null);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        for (var task : taskList) {
            var production = manuallyReportExecuteRepository.listProductionByTaskId(task.getId());
            if (CollectionUtils.isEmpty(production)) {
                continue;
            }
            var quantity = executeDomainService.summaryQuantity(production);

            task.setProduceQuantity(quantity.getLeft());
            var productionDateMap = production.stream().collect(Collectors.groupingBy(it -> it.getDate().value()));
            var latestDate = productionDateMap.keySet().stream().max(String::compareTo).orElse(null);
            if (null != latestDate) {
                var dateProduction = productionDateMap.get(latestDate);
                var baseLineTime = TimeUtil.dateFromYYYYMMDD(latestDate)
                        .atTime(dateProduction.size() == 1 ? PlanConstant.FIRST_SHIFT_END_HOUR : PlanConstant.SECOND_SHIFT_END_HOUR, 0, 0);
                var component = styleRepository.getComponentBySkuAndPart(task.getProduceOrderCode(), task.getSkuCode(), task.getPart());
                task.estimateEndTime(baseLineTime, component);
            }

            machineTaskRepository.saveTask(task, false);
        }
    }

}
