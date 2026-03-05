package com.santoni.iot.aps.domain.plan.entity.machine;

import com.santoni.iot.aps.domain.plan.entity.valueobj.MachineUsage;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanStartTime;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.AvailableTime;
import com.santoni.iot.aps.domain.support.entity.DatePeriod;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import com.santoni.iot.aps.domain.support.service.TimePeriodService;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class MachinePlan {

    private Machine machine;

    private List<PlannedTask> taskList;

    private AvailableTime availableTime;

    public List<TaskSegment> expandPlannedTasks() {
        if (CollectionUtils.isEmpty(taskList)) {
            return Lists.newArrayList();
        }
        return expandTaskAndSort(taskList.stream());
    }

    public List<Long> calculateAvailableTimeBySegment(TimePeriodService timePeriodService, LocalDateTime startTime, List<LocalDateTime> endTimes) {
        if (CollectionUtils.isEmpty(taskList)) {
            return timePeriodService.calculateAvailableTimeByPeriod(List.of(), startTime, endTimes);
        }
        var allTimePeriods = this.expandAllSegmentAsTimePeriod();

        return timePeriodService.calculateAvailableTimeByPeriod(allTimePeriods, startTime, endTimes);
    }

    private List<TaskSegment> expandTaskAndSort(Stream<PlannedTask> plannedTasks) {
        return plannedTasks.map(PlannedTask::getSegments)
                .flatMap(List::stream)
                .sorted((seg1, seg2) -> seg1.getPlan().compareByStart(seg2.getPlan()))
                .toList();
    }

    public Optional<PlanStartTime> getPlanStartTime() {
        return Optional.ofNullable(CollectionUtils.isEmpty(taskList) ? null :
                taskList.get(0).getPlan().getPeriod().getStart());
    }

    public Optional<PlanEndTime> getPlanEndTime() {
        if (CollectionUtils.isEmpty(taskList)) {
            return Optional.empty();
        }
        LocalDateTime last = null;
        for (var task : taskList) {
            if (null == last) {
                last = task.getPlan().getPeriod().getEnd().value();
            } else {
                if (last.isBefore(task.getPlan().getPeriod().getEnd().value())) {
                    last = task.getPlan().getPeriod().getEnd().value();
                }
            }
        }
        return Optional.ofNullable(null == last ? null : new PlanEndTime(last));
    }

    public int compareByAvailableTime(MachinePlan other) {
        if (this.availableTime.getTotalSeconds() > other.availableTime.getTotalSeconds()) {
            return -1;
        }
        return 1;
    }

    public void calculateAvailableTimePeriod(TimePeriodService timePeriodService, LocalDateTime startTime, LocalDateTime endTime) {
        if (null != availableTime) {
            return;
        }
        if (CollectionUtils.isEmpty(taskList)) {
            this.availableTime = new AvailableTime(startTime, endTime);
            return;
        }
        var allTimePeriods = this.expandAllSegmentAsTimePeriod();

        this.availableTime = timePeriodService.calculateAvailableTime(allTimePeriods, startTime, endTime);
    }

    private List<TimePeriod> expandAllSegmentAsTimePeriod() {
        if (CollectionUtils.isEmpty(taskList)) {
            return Lists.newArrayList();
        }
        return taskList.stream().map(PlannedTask::getSegments)
                .flatMap(List::stream)
                .map(it -> it.getPlan().getPeriod().toTimePeriod())
                .sorted(Comparator.comparing(it -> it.getStart().value()))
                .toList();
    }

    public boolean canArrange(ProductionPlan plan) {
        if (CollectionUtils.isEmpty(taskList)) {
            return true;
        }
        var allSegments = this.expandPlannedTasks();
        if (allSegments.get(allSegments.size() - 1).getPlan().endEarlierThanStart(plan)) {
            return true;
        }
        if (allSegments.get(0).getPlan().startLaterThanEnd(plan)) {
            return true;
        }
        for (TaskSegment segment : allSegments) {
            if (segment.getPlan().endEarlierThanStart(plan)) {
                continue;
            }
            if (!segment.getPlan().startLaterThanEnd(plan)) {
                return false;
            }
        }
        return true;
    }

    public static MachinePlan of(Machine machine, List<PlannedTask> taskList) {
        return new MachinePlan(machine, taskList, (t1, t2) -> t1.getPlan().compareByStart(t2.getPlan()));
    }

    public MachinePlan(Machine machine,
                       List<PlannedTask> taskList,
                       Comparator<PlannedTask> comparator) {
        this.machine = machine;
        if (CollectionUtils.isNotEmpty(taskList)) {
            this.taskList = taskList;
            this.taskList.sort(comparator);
        } else {
            this.taskList = Lists.newArrayList();
        }
    }

    public void calculateUsage(List<DatePeriod> datePeriods, MachineUsage usage) {
        var allPeriods = this.expandAllSegmentAsTimePeriod();
        if (CollectionUtils.isEmpty(allPeriods)) {
            for (var date : datePeriods) {
                usage.countOne(date.getDate());
            }
            return;
        }
        int periodIndex = 0, dateIndex = 0;
        while (periodIndex < allPeriods.size() && dateIndex < datePeriods.size()) {
            var period = allPeriods.get(periodIndex);

            if (datePeriods.get(dateIndex).getEnd().value().isBefore(period.getStart().value())) {
                usage.countOne(datePeriods.get(dateIndex).getDate());
                dateIndex++;
                continue;
            }
            for (int i = dateIndex; i < datePeriods.size(); i++) {
                if (datePeriods.get(i).getStart().value().isAfter(period.getEnd().value())) {
                    dateIndex = i;
                    break;
                } else if (i == datePeriods.size() - 1) {
                    usage.countOneInUse(datePeriods.get(i).getDate());
                    dateIndex = datePeriods.size();
                    break;
                } else {
                    usage.countOneInUse(datePeriods.get(i).getDate());
                }
            }
            periodIndex++;
        }
        if (dateIndex <= datePeriods.size() - 1) {
            for (int i = dateIndex; i < datePeriods.size(); i++) {
                usage.countOne(datePeriods.get(i).getDate());
            }
        }
    }
}
