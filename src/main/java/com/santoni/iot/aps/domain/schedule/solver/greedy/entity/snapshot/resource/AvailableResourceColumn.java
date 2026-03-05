package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.resource;

import com.santoni.iot.aps.common.utils.TriMap;
import lombok.Getter;

import java.time.LocalDateTime;

public class AvailableResourceColumn {

    @Getter
    private LocalDateTime endTime;

    private final TriMap<Integer, Integer, Boolean, Double> availableTime = new TriMap<>();

    public AvailableResourceColumn(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void accumulateTime(Integer cylinderDiameter, Integer needleSpacing, Boolean bareSpandex, Double time) {
        var existTime = availableTime.get(cylinderDiameter, needleSpacing, bareSpandex);
        if (null == existTime) {
            availableTime.put(cylinderDiameter, needleSpacing, bareSpandex, time);
        } else {
            availableTime.put(cylinderDiameter, needleSpacing, bareSpandex, existTime + time);
        }
    }

    public boolean hasLeftTime(Integer cylinderDiameter, Integer needleSpacing, Boolean bareSpandex) {
        var leftTime = availableTime.get(cylinderDiameter, needleSpacing, bareSpandex);
        return null != leftTime && Double.compare(leftTime, 0) > 0;
    }

    public double occupyTimeAsMoreAsPossible(Integer cylinderDiameter, Integer needleSpacing, Boolean bareSpandex, Double time) {
        var leftTime = availableTime.get(cylinderDiameter, needleSpacing, bareSpandex);
        if (null == leftTime) {
            return 0.0;
        }
        if (Double.compare(leftTime, time) < 0) {
            availableTime.put(cylinderDiameter, needleSpacing, bareSpandex, 0.0);
            return leftTime;
        } else {
            availableTime.put(cylinderDiameter, needleSpacing, bareSpandex, leftTime - time);
            return time;
        }
    }

    public boolean occupyTime(Integer cylinderDiameter, Integer needleSpacing, Boolean bareSpandex, Double time) {
        if (bareSpandex) {
            return occupyBareSpandexTime(cylinderDiameter, needleSpacing, time);
        }
        var existTime = availableTime.get(cylinderDiameter, needleSpacing, false);
        if (null != existTime && Double.compare(existTime, time) > 0) {
            availableTime.put(cylinderDiameter, needleSpacing, false, existTime - time);
            return true;
        }
        if (null == existTime) {
            return occupyBareSpandexTime(cylinderDiameter, needleSpacing, time);
        } else {
            var bareSpandexTime = availableTime.get(cylinderDiameter, needleSpacing, true);
            if (null == bareSpandexTime) {
                return false;
            }
            var leftTime = time - existTime;
            if (Double.compare(bareSpandexTime, leftTime) < 0) {
                return false;
            }
            availableTime.put(cylinderDiameter, needleSpacing, false, 0.0);
            availableTime.put(cylinderDiameter, needleSpacing, true, bareSpandexTime - leftTime);
            return true;
        }
    }

    private boolean occupyBareSpandexTime(Integer cylinderDiameter, Integer needleSpacing, Double time) {
        var bareSpandexTime = availableTime.get(cylinderDiameter, needleSpacing, true);
        if (null == bareSpandexTime || Double.compare(bareSpandexTime, time) < 0) {
            return false;
        }
        availableTime.put(cylinderDiameter, needleSpacing, true, bareSpandexTime - time);
        return true;
    }
}
