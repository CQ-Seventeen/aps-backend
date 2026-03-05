package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.ArrangeMidResult;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.OccupiedTimeByNode;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.EndTimeColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

public class AvailableResourceTable {

    private TreeMap<LocalDateTime, AvailableResourceColumn> resourceMap = Maps.newTreeMap(LocalDateTime::compareTo);

    private List<LocalDateTime> allTimeNode = Lists.newArrayList();

    public void accumulateTime(LocalDateTime endTime,
                               Integer cylinderDiameter,
                               Integer needleSpacing,
                               Boolean bareSpandex,
                               Double time) {
        var col = resourceMap.get(endTime);
        if (null == col) {
            var newCol = new AvailableResourceColumn(endTime);
            newCol.accumulateTime(cylinderDiameter, needleSpacing, bareSpandex, time);
            this.add(newCol);
        } else {
            col.accumulateTime(cylinderDiameter, needleSpacing, bareSpandex, time);
        }
    }

    public void arrangeLeftTime(ArrangeMidResult midResult) {
        if (midResult.getLeftTimeRequire().isEmpty()) {
            return;
        }
        for (var entry : midResult.getLeftTimeRequire()) {

        }
    }

    public ArrangeMidResult arrangeRequire(EndTimeColumn col) {
        var result = new ArrangeMidResult(col);
        int index = allTimeNode.indexOf(col.getEndTime());
        if (index == -1) {
            return result;
        }
        boolean canSatisfy = true;
        for (var entry : col.getNeededTime()) {
            var occupyRes = tryOccupyTime(index, entry.getKey1(), entry.getKey2(), entry.getKey3(), entry.getValue());
            result.occupyTimePeriod(entry.getKey1(), entry.getKey2(), entry.getKey3(), occupyRes.getLeft());
            if (Double.compare(occupyRes.getRight(), 0.0) > 0) {
                result.recordLeftTime(entry.getKey1(), entry.getKey2(), entry.getKey3(), occupyRes.getRight());
                canSatisfy = false;
            }
        }
        if (canSatisfy) {
            result.satisfied();
        }
        return result;
    }

    private Pair<OccupiedTimeByNode, Double> tryOccupyTime(int nodeIndex,
                                                           Integer cylinderDiameter,
                                                           Integer needleSpacing,
                                                           Boolean bareSpandex,
                                                           Double neededTime) {
        double leftTime = neededTime;
        var occupiedTime = new OccupiedTimeByNode(allTimeNode.size());
        double[] timeArr = new double[allTimeNode.size()];
        for (int i = 0; i < nodeIndex; i++) {
            var col = resourceMap.get(allTimeNode.get(i));
            if (col.hasLeftTime(cylinderDiameter, needleSpacing, bareSpandex)) {
                var occTime = col.occupyTimeAsMoreAsPossible(cylinderDiameter, needleSpacing, true, leftTime);
                timeArr[i] = occTime;
                if (Double.compare(leftTime, occTime) <= 0) {
                    occupiedTime.occupyTime(timeArr, bareSpandex);
                    return Pair.of(occupiedTime, 0.0);
                }
                leftTime -= occTime;
            }
        }
        var col = resourceMap.get(allTimeNode.get(nodeIndex));
        var occTime = col.occupyTimeAsMoreAsPossible(cylinderDiameter, needleSpacing, bareSpandex, leftTime);
        timeArr[nodeIndex] = occTime;
        occupiedTime.occupyTime(timeArr, bareSpandex);
        return Pair.of(occupiedTime, leftTime - occTime);
    }

    private void add(AvailableResourceColumn column) {
        resourceMap.put(column.getEndTime(), column);
        allTimeNode.add(column.getEndTime());
        allTimeNode.sort(LocalDateTime::compareTo);
    }
}
