package com.santoni.iot.aps.domain.resource.entity;

import com.santoni.iot.aps.common.utils.ListOperateUtil;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttr;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineAttrValue;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class MachineFeature {

    private MachineAttr attr;

    private List<MachineAttrValue> valueList;

    public static MachineFeature of(String attrCode, List<String> attrValues) {
        return new MachineFeature(new MachineAttr(attrCode), attrValues.stream().map(MachineAttrValue::new).toList());
    }

    public MachineFeature(MachineAttr attr, List<MachineAttrValue> valueList) {
        this.attr = attr;
        this.valueList = valueList;
    }

    public void intersectWith(MachineFeature other) {
        this.valueList = ListOperateUtil.operate(this.valueList, other.valueList, MachineAttrValue::value, true);
    }

    public void unionWith(MachineFeature other) {
        this.valueList = ListOperateUtil.operate(this.valueList, other.valueList, MachineAttrValue::value, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineFeature that)) return false;
        return Objects.equals(attr, that.attr) && CollectionUtils.isEqualCollection(valueList, that.valueList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attr, valueList);
    }

    @Override
    public String toString() {
        return attr.code() + ":"
                + valueList.stream().map(MachineAttrValue::value).collect(Collectors.joining(","));
    }
}
