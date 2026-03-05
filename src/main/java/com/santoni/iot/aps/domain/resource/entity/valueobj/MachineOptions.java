package com.santoni.iot.aps.domain.resource.entity.valueobj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.common.utils.ListOperateUtil;
import com.santoni.iot.aps.domain.bom.entity.MachineRequirement;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class MachineOptions {

    @Getter
    private List<MachineSize> machineSizeList;

    private List<MachineType> typeList;

    private List<BareSpandex> bareSpandexList;

    private List<HighSpeed> highSpeedList;

    @Getter
    private List<MachineFeature> featureList;

    private List<MachineArea> machineAreaList;

    public MachineOptions(List<MachineSize> machineSizeList,
                          List<MachineType> typeList,
                          List<BareSpandex> bareSpandexList,
                          List<HighSpeed> highSpeedList,
                          List<MachineFeature> featureList,
                          List<MachineArea> machineAreaList) {
        this.machineSizeList = CollectionUtils.isEmpty(machineSizeList) ? List.of() : machineSizeList;
        this.typeList = CollectionUtils.isEmpty(typeList) ? List.of() : typeList;
        this.bareSpandexList = CollectionUtils.isEmpty(bareSpandexList) ? List.of() : bareSpandexList;
        this.highSpeedList = CollectionUtils.isEmpty(highSpeedList) ? List.of() : highSpeedList;
        this.featureList = CollectionUtils.isEmpty(featureList) ? List.of() : featureList;
        this.machineAreaList =  CollectionUtils.isEmpty(machineAreaList) ? List.of() : machineAreaList;
    }

    public void unionRequirement(MachineRequirement requirement) {
        if (null == requirement) {
            return;
        }
        this.typeList = ListOperateUtil.operate(this.typeList, requirement.getTypeList(), MachineType::value, false);
        this.bareSpandexList = ListOperateUtil.operate(this.bareSpandexList, requirement.getBareSpandexList(), BareSpandex::value, false);
        if (null != requirement.getHighSpeed()) {
            this.highSpeedList = ListOperateUtil.operate(this.highSpeedList, Lists.newArrayList(requirement.getHighSpeed()), HighSpeed::value, false);
        }
        unionFeature(requirement.getFeatureList());
    }

    public void unionMachineSize(MachineSize machineSize) {
        operateSize(Lists.newArrayList(machineSize), false);
    }

    public void unionWith(MachineOptions other) {
        if (null == other) {
            return;
        }
        operateSize(other.machineSizeList, false);
        this.typeList = ListOperateUtil.operate(this.typeList, other.typeList, MachineType::value, false);
        this.bareSpandexList = ListOperateUtil.operate(this.bareSpandexList, other.bareSpandexList, BareSpandex::value, false);
        this.highSpeedList = ListOperateUtil.operate(this.highSpeedList, other.highSpeedList, HighSpeed::value, false);
        unionFeature(other.featureList);
    }

    public void intersectWith(MachineOptions other) {
        if (null == other) {
            return;
        }
        operateSize(other.machineSizeList, true);
        this.typeList = ListOperateUtil.operate(this.typeList, other.typeList, MachineType::value, true);
        this.bareSpandexList = ListOperateUtil.operate(this.bareSpandexList, other.bareSpandexList, BareSpandex::value, true);
        this.highSpeedList = ListOperateUtil.operate(this.highSpeedList, other.highSpeedList, HighSpeed::value, true);
        intersectFeature(other.featureList);
    }

    private void operateSize(List<MachineSize> other, boolean isIntersect) {
        if (CollectionUtils.isEmpty(other)) {
            return;
        }
        if (CollectionUtils.isEmpty(machineSizeList)) {
            this.machineSizeList = other;
            return;
        }
        Map<Integer, Set<Integer>> cylinderNeedleSpacingMap = pairCylinderDiameterAndNeedleSpacingMap(this.machineSizeList);
        List<MachineSize> result = Lists.newArrayList();
        for (var size : other) {
            var needleSpacingSet = cylinderNeedleSpacingMap.get(size.getCylinderDiameter().value());
            if (isIntersect) {
                if (CollectionUtils.isNotEmpty(needleSpacingSet) && needleSpacingSet.contains(size.getNeedleSpacing().value())) {
                    result.add(size);
                }
            } else {
                if (CollectionUtils.isEmpty(needleSpacingSet) || !needleSpacingSet.contains(size.getNeedleSpacing().value())) {
                    result.add(size);
                }
            }
        }
        if (!isIntersect) {
            result.addAll(this.machineSizeList);
        }
        this.machineSizeList = result;
    }

    private void intersectFeature(List<MachineFeature> other) {
        var context = prepareOperateFeatureContext(other);
        if (null != context.getLeft()) {
            this.featureList = context.getLeft();
            return;
        }
        var featureMap = context.getRight();
        List<MachineFeature> result = Lists.newArrayList();
        for (var feature : other) {
            var thisFeature = featureMap.get(feature.getAttr().code());
            if (null != thisFeature) {
                thisFeature.intersectWith(feature);
                result.add(thisFeature);
            }
        }
        this.featureList = result;
    }

    private Pair<List<MachineFeature>, Map<String, MachineFeature>> prepareOperateFeatureContext(List<MachineFeature> other) {
        if (CollectionUtils.isEmpty(other)) {
            return Pair.of(this.featureList, null);
        }
        if (CollectionUtils.isEmpty(featureList)) {
            return Pair.of(other, null);
        }
        return Pair.of(null, this.featureList.stream()
                .collect(Collectors.toMap(it -> it.getAttr().code(),
                        it -> it,
                        (v1, v2) -> v1)));
    }

    private void unionFeature(List<MachineFeature> other) {
        var context = prepareOperateFeatureContext(other);
        if (null != context.getLeft()) {
            this.featureList = context.getLeft();
            return;
        }
        var featureMap = context.getRight();
        List<MachineFeature> result = Lists.newArrayList();
        for (var feature : other) {
            var thisFeature = featureMap.get(feature.getAttr().code());
            if (null != thisFeature) {
                thisFeature.unionWith(feature);
                result.add(thisFeature);
                featureMap.remove(feature.getAttr().code());
            } else {
                result.add(feature);
            }
        }
        if (MapUtils.isNotEmpty(featureMap)) {
            result.addAll(featureMap.values());
        }
        this.featureList = result;
    }

    public static MachineOptions empty() {
        return new MachineOptions(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public List<Integer> getNeedleSpacingList() {
        return this.machineSizeList.stream().map(it -> it.getNeedleSpacing().value()).distinct().toList();
    }

    public Map<Integer, List<Integer>> pairCylinderDiameterAndNeedleNumber() {
        return pairCylinderDiameterAndNeedleNumberMap(this.machineSizeList).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().stream().toList()
        ));
    }

    private Map<Integer, Set<Integer>> pairCylinderDiameterAndNeedleSpacingMap(List<MachineSize> machineSizeList) {
        Map<Integer, Set<Integer>> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(machineSizeList)) {
            for (var size : machineSizeList) {
                var needleSpacingSet = result.get(size.getCylinderDiameter().value());
                if (null == needleSpacingSet) {
                    result.put(size.getCylinderDiameter().value(), Sets.newHashSet(size.getNeedleSpacing().value()));
                } else {
                    needleSpacingSet.add(size.getNeedleSpacing().value());
                }
            }
        }
        return result;
    }

    private Map<Integer, Set<Integer>> pairCylinderDiameterAndNeedleNumberMap(List<MachineSize> machineSizeList) {
        Map<Integer, Set<Integer>> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(machineSizeList)) {
            for (var size : machineSizeList) {
                var needleNumberSet = result.get(size.getCylinderDiameter().value());
                if (null == needleNumberSet) {
                    result.put(size.getCylinderDiameter().value(), Sets.newHashSet(size.getNeedleNumber().value()));
                } else {
                    needleNumberSet.add(size.getNeedleNumber().value());
                }
            }
        }
        return result;
    }

    public List<String> getMachineTypeList() {
        if (CollectionUtils.isEmpty(typeList)) {
            return List.of();
        }
        return typeList.stream().map(MachineType::value).toList();
    }

    public List<String> getBareSpandexList() {
        if (CollectionUtils.isEmpty(bareSpandexList)) {
            return List.of();
        }
        return bareSpandexList.stream().map(BareSpandex::value).toList();
    }

    public List<Boolean> getHighSpeedList() {
        if (CollectionUtils.isEmpty(highSpeedList)) {
            return List.of();
        }
        return highSpeedList.stream().map(HighSpeed::value).toList();
    }

    public List<String> getMachineAreaList() {
        if (CollectionUtils.isEmpty(machineAreaList)) {
            return List.of();
        }
        return machineAreaList.stream().map(MachineArea::value).toList();
    }
}
