package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.BareSpandex;
import com.santoni.iot.aps.domain.resource.entity.valueobj.HighSpeed;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineType;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class MachineRequirement {

    private List<MachineType> typeList;

    private List<BareSpandex> bareSpandexList;

    private HighSpeed highSpeed;

    private List<MachineFeature> featureList;

    public MachineRequirement(List<MachineType> typeList,
                              List<BareSpandex> bareSpandexList,
                              HighSpeed highSpeed,
                              List<MachineFeature> featureList) {
        this.typeList = typeList;
        this.bareSpandexList = bareSpandexList;
        this.highSpeed = highSpeed;
        this.featureList = featureList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineRequirement that)) return false;
        return CollectionUtils.isEqualCollection(typeList, that.typeList) &&
                CollectionUtils.isEqualCollection(bareSpandexList, that.bareSpandexList) &&
                CollectionUtils.isEqualCollection(featureList, that.featureList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeList, bareSpandexList, highSpeed, featureList);
    }

    @Override
    public String toString() {
        return (CollectionUtils.isNotEmpty(typeList) ?
                        typeList.stream().map(MachineType::value).collect(Collectors.joining(",")) : "") +
                (CollectionUtils.isNotEmpty(bareSpandexList) ?
                        bareSpandexList.stream().map(BareSpandex::value).collect(Collectors.joining(",")) : "") +
                (null == highSpeed ? "" : highSpeed.toString()) +
                (CollectionUtils.isNotEmpty(featureList) ? featureList.stream().map(MachineFeature::toString).collect(Collectors.joining(";")) : "");
    }
}
