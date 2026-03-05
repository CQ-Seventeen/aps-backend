package com.santoni.iot.aps.domain.resource.entity.valueobj;

import com.santoni.iot.aps.domain.support.entity.valueobj.OptionType;
import lombok.Getter;

import java.util.List;

public class MachineAttrConfig {

    @Getter
    private MachineAttr attr;

    @Getter
    private List<MachineAttrValue> optionalValues;

    @Getter
    private OptionType optionType;

    private UseToFilter useToFilter;

    public MachineAttrConfig(MachineAttr attr,
                             List<MachineAttrValue> optionalValues,
                             OptionType optionType,
                             UseToFilter useToFilter) {
        this.attr = attr;
        this.optionalValues = optionalValues;
        this.optionType = optionType;
        this.useToFilter = null == useToFilter ? UseToFilter.no() : useToFilter;
    }

    public boolean useToFilter() {
        return null != useToFilter && useToFilter.value();
    }

    public boolean singleSelect() {
        return optionType == OptionType.SINGLE;
    }

    public boolean multiSelect() {
        return optionType == OptionType.MULTIPLE;
    }
}
