package com.santoni.iot.aps.infrastructure.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MachineAttrConfigPO extends BasePO {

    private Long id;

    private long instituteId;

    private String attrCode;

    private String optionalValues;

    private int optionType;

    private boolean useToFilter;
}
