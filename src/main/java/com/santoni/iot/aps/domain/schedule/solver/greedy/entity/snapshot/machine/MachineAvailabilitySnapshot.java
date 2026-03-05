package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.machine;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.support.entity.TimePeriod;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
public class MachineAvailabilitySnapshot {

    private long machineId;

    private List<TimePeriodSnapshot> periods;

    private int[] periodStatus;

    private List<Long> leftTimeList;

    private long totalLeftTime;

    public MachineAvailabilitySnapshot(long machineId,
                                       List<TimePeriod> periods,
                                       List<Long> leftTimeList) {
        this.machineId = machineId;
        this.periods = periods.stream().map(it -> new TimePeriodSnapshot(it.getStart().value(),
                it.getEnd().value(), it.getSeconds())).toList();
        this.periodStatus = new int[periods.size()];
        Arrays.fill(this.periodStatus, 1);
        this.leftTimeList = leftTimeList;
        this.totalLeftTime = leftTimeList.stream().mapToLong(Long::longValue).sum();
    }

    public Pair<Double, List<TimePeriod>> arrangeTime(double time, int index, LocalDateTime endTime) {
        long leftTime = 0;
        for (int i = 0; i <= index; i++) {
            leftTime += this.leftTimeList.get(i);
        }
        if (Double.compare(time, leftTime) > 0) {
            var timePeriods = occupyTimePeriod(endTime);
            for (int i = 0; i <= index; i++) {
                leftTimeList.set(i, 0L);
            }
            totalLeftTime -= leftTime;
            return Pair.of(time - leftTime, timePeriods);
        }
        double timeCopy = time;
        var timePeriods = occupyTimePeriod(time);
        for (int i = 1; i <= index; i++) {
            if (leftTimeList.get(i) < timeCopy) {
                timeCopy -= leftTimeList.get(i);
                leftTimeList.set(i, 0L);
            } else {
                leftTimeList.set(i, (long) Math.floor(leftTimeList.get(i) - timeCopy));
                break;
            }
        }
        totalLeftTime -= time;
        return Pair.of(0.0, timePeriods);
    }

    public Triple<Integer, Integer, List<TimePeriod>> arrangeTimeByQuantity(double unitTime, int demandQuantity, int index, LocalDateTime endTime) {
        // 计算可用总时间
        long availableTime = 0;
        for (int i = 0; i <= index; i++) {
            availableTime += this.leftTimeList.get(i);
        }
        
        // 如果可用时间小于一件的时间，不进行分配
        if (Double.compare(availableTime, unitTime) < 0) {
            return Triple.of(demandQuantity, 0, Lists.newArrayList());
        }
        
        // 计算需求总时间
        double demandTotalTime = unitTime * demandQuantity;
        
        List<TimePeriod> allocatedPeriods;
        int actualAllocatedQuantity;
        double actualAllocatedTime;
        
        if (Double.compare(availableTime, demandTotalTime) >= 0) {
            // 可用时间 >= 需求时间：分配需求的全部时间
            actualAllocatedTime = demandTotalTime;
            actualAllocatedQuantity = demandQuantity;
            allocatedPeriods = occupyTimePeriod(actualAllocatedTime);
            
            // 更新剩余时间
            updateLeftTime(actualAllocatedTime, index);
            
            return Triple.of(0, actualAllocatedQuantity, allocatedPeriods);
        } else {
            // 可用时间 < 需求时间：全部分配，数量向下取整
            actualAllocatedTime = availableTime;
            actualAllocatedQuantity = (int) Math.floor(availableTime / unitTime);
            allocatedPeriods = occupyTimePeriod(actualAllocatedTime);
            
            // 更新剩余时间（全部用完）
            updateLeftTime(actualAllocatedTime, index);
            
            int remainingQuantity = demandQuantity - actualAllocatedQuantity;
            return Triple.of(remainingQuantity, actualAllocatedQuantity, allocatedPeriods);
        }
    }
    
    private void updateLeftTime(double usedTime, int index) {
        double remainingUsedTime = usedTime;
        
        for (int i = 0; i <= index; i++) {
            if (Double.compare(remainingUsedTime, 0) <= 0) {
                break;
            }
            
            long currentLeftTime = this.leftTimeList.get(i);
            if (Double.compare(currentLeftTime, remainingUsedTime) <= 0) {
                // 当前时间段全部用完
                remainingUsedTime -= currentLeftTime;
                this.leftTimeList.set(i, 0L);
                this.totalLeftTime -= currentLeftTime;
            } else {
                // 当前时间段部分使用
                long newLeftTime = (long) Math.floor(currentLeftTime - remainingUsedTime);
                this.leftTimeList.set(i, newLeftTime);
                this.totalLeftTime -= (long) Math.ceil(remainingUsedTime);
                remainingUsedTime = 0;
            }
        }
    }

    private List<TimePeriod> occupyTimePeriod(double time) {
        List<TimePeriod> res = Lists.newArrayList();
        for (int i = 0; i < periods.size(); i++) {
            if (periodStatus[i] == -1) {
                continue;
            }
            if (Double.compare(periods.get(i).getTotalTime(), time) < 0) {
                time -= periods.get(i).getTotalTime();
                res.add(periods.get(i).toTimePeriod());
                periodStatus[i] = -1;
            } else {
                res.add(periods.get(i).occupyTime(time));
                break;
            }
        }
        return res;
    }

    private List<TimePeriod> occupyTimePeriod(LocalDateTime endTime) {
        List<TimePeriod> res = Lists.newArrayList();
        for (int i = 0; i < periods.size(); i++) {
            if (periodStatus[i] == -1) {
                continue;
            }
            if (periods.get(i).getEnd().isBefore(endTime)) {
                res.add(periods.get(i).toTimePeriod());
                periodStatus[i] = -1;
            } else {
                res.add(periods.get(i).splitBy(endTime));
                break;
            }
        }
        return res;
    }
}
